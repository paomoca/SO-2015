package so.filesystem.disk;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

import so.filesystem.general.CONFIG;
import so.filesystem.general.FreeSpaceManager;

public class DiskController {
	
	private int METADATA_LENGTH;
	private boolean NEW_DISK = false;
	
	byte[] systemKey = "0KYHNzQbaIRPe6APYx3NLg5kX3VsEvN52eOYAmJl7YlrahtGOI8xy22RrUUhMyMtPNYWAOEfes376IQIMmHPf1JUE8cwm26xBTN69pF9hCk6jc2f23mXNE2Z8CQLEU9RcJGGXO9Zb3vgzyHB2JhQs0zNf08DEPt6kEWgB0yrXpCcYoljprSVtjGh2rJfJOXmSR2oz8gSADU7y7nBPhznVGMKCKyeCfiUCQWIZ4BHCqq1ul4P1Nlcj9g1K8YorxCF3NAsMELBMlINU9oaNgc04nAX7VqmulOOhAUUCs4D2AV1yJ8aQSkjloBkwBp8StAWgpaP9fuuy4FBKGtHeTRwf4XNZqT3TyNre0PLP7usiijDo2RaXDARleKaEbsISYQBUBUYhrH3QtPV2qJ7bwtcj2uAvlUP4KSDSE5yAuGEoSXs69quHk3OR2aWarXhuvQ7kVeKincMNzo2UzR0XRuW5zq1K1vl0yWR4jXaGurzyjYG0JYRlPk5Ba2sDjHmSOakgjGPAD55OxRTc5ztglNmSXMjU8zrSEGa1slM5LjjxI9e1HUVD4yMc0QZBC6PtLI6snkWGq8fWHTm22q797O5vOyjWiQlDwee2RQM3vreKX1s85mMmgy6a3wzIREuH2ivwJjimp3KjUyUZe3h5tkhQtRnMSfCqw57IZ5IGbOt9b3to44Ghj5k3T8RUX43G0cheT81DYT1VpATlAgQlXCARjyBDEpIuiAB6x9Nfpg1b5SskvJ3oM7Kyl0AM9GTlZOLy1tNRtFejtgfwvZm7inWEFK1AGzftB2RsGlODbKaaNDErZaqYFKnxAkcsmJ0pQtQLo25fUYS9N577F8Hs6BrFc7axv4kkIbLUytFvNUAkIwpWsbTDJ4fCQz9VQXChO4Qsog8wxCILspfR5qb0QNV9a9V8zUYVnSKjBVCtPyyrZpXqz1WmhuYUaWa0XNyKR45wyN2mjXyfOlopxtwhK3BsXBs5jrnREuQMl83gpWxDHTzNYjOgiJCU6eYhAFxjBOl".getBytes();

	private RandomAccessFile rawDeviceRW;
	private DiskDirectory directory;
		
	public DiskController(boolean formatFlag) throws DiskControllerException, DiskFormatException{
		
		try {
			
			//The order is important please do not modify.
			mountDevice();
			if(formatFlag){
				formatDevice();
			}else{
				deviceIdentification();
			}
			directoryInitialization();
			freeSpaceManagerInitialization();
			
			
		} catch (DeviceInitializationException e) {
			
			throw new DiskControllerException(e.toString());
		}

	}
	
	/**********************************
	 *    Initialization Functions
	 *********************************/
	
	private void mountDevice() throws DeviceInitializationException{
		
		//Reference to the raw device	
		try {
			rawDeviceRW = new RandomAccessFile(CONFIG.DISK_LOCATION, "rw");
		} catch (FileNotFoundException e) {
			
			throw new DeviceInitializationException("Unable to mount device.");
			
		}
	}
	
	private void deviceIdentification() throws DeviceInitializationException, DiskFormatException{
		
		//Initialize METADATA_LENGTH in 0 so we can start reading in the actual position 0. 
		//(Check addressTranslation function)
		METADATA_LENGTH = 0;
		byte[] inKey;
		
		try {
			
			inKey = rawRead(0, 0, systemKey.length);
			
			if(!Arrays.equals(systemKey, inKey)){
				
				//TODO: ESCRIBIR EL KEY AL PRINCIPIO
				NEW_DISK = true;
				
				throw new DiskFormatException("This device is already initialized with a different FileSystem");
				
			}
			
			//If everything went well we add the size of the key to METADATA_LENGTH-1 because we start in 0.
			METADATA_LENGTH += systemKey.length-1;
			
		} catch (DiskControllerException e) {
			throw new DeviceInitializationException("An error occured trying to identify the device.");
		}
		
	}
	
	public void formatDevice() throws DeviceInitializationException, DiskFormatException {
		METADATA_LENGTH = 0;

		try {

			rawWrite(0, 0, systemKey);
			
			NEW_DISK = true;

			// If everything went well we add the size of the key to
			// METADATA_LENGTH.
			METADATA_LENGTH += systemKey.length;

		} catch (DiskControllerException e) {
			throw new DiskFormatException(
					"An error occured trying formatting the device.");
		}
	}
	
	private void directoryInitialization(){
		
		
	}
	
	private void freeSpaceManagerInitialization() throws DiskControllerException{
	
		try {
			
			if(NEW_DISK){
				//If a new disk is being used the Disk Free Space Manager is initialized with the total Disk Available Space in int.
				int numberOfBlocks = (int) ((rawDeviceRW.length()-METADATA_LENGTH)/CONFIG.BLOCK_SIZE);

				//Disk Free Space Manager is initialized but never used within this class.
				DiskFreeSpaceManager.getInstance(numberOfBlocks);
				
			} else {
				
				/* If it is not a new disk and the disk already contains free space information,
				 * the Disk Free Space Manager is initialized with with that information. */
				
				// 1. First we get information about how many blocks contain the Free Space Manager data.
				byte[] freeSpaceManagerBlockDataSize = rawRead(0,0,CONFIG.FREE_SPACE_MANAGER_INITIAL_CONTROL_BYTE_SIZE);
				int numberOfBlocksToRead = byteAddressToInt(freeSpaceManagerBlockDataSize);

				// 2. Next we read that amount of blocks.
				byte[] freeSpaceManagerData = rawReadBlock(0,0);
				
				// 3. We use the data we read to initialize the DiskFreeSpaceManager
				DiskFreeSpaceManager.getInstance(freeSpaceManagerData);
				
				METADATA_LENGTH += CONFIG.FREE_SPACE_MANAGER_INITIAL_CONTROL_BYTE_SIZE;
				
			}
			
		} catch (IOException e) {
			
			throw new DiskControllerException("Unable to initialize Free Space Manager.");
		}

		
	}
	
	/**********************************
	 *    Raw Read-Write
	 *********************************/
	
	//Function used whenever we need to write to disk. This is the only function that actually writes into the raw device.
	public void rawWrite(int address, int offset, byte[] dataToWrite) throws DiskControllerException{
		
		try {
			int position = addressTranslation(address, offset);
			rawDeviceRW.seek(position);
			rawDeviceRW.write(dataToWrite, 0, dataToWrite.length);
		} catch (IOException e) {
		
			throw new DiskControllerException("");
		}
		
	}
	
	public byte[] rawRead(int address, int offset, int length) throws DiskControllerException{
		
		byte[] readBuffer = new byte[length];
		int position = addressTranslation(address, offset);
		
		try {
			rawDeviceRW.seek(position);
			rawDeviceRW.read(readBuffer, 0 , readBuffer.length);
			return readBuffer;
		} catch (IOException e) {
			
			throw new DiskControllerException("");
		}
		
		
	}
	
public void rawWriteBlock(int address, int offset, byte[] dataToWrite) throws DiskControllerException{
		
		try {
			
			if(dataToWrite.length > CONFIG.BLOCK_SIZE){
				throw new DiskControllerException("An error occured trying to write a block, "
						+ "data[] size did not correspond to "+CONFIG.BLOCK_SIZE);
			}
			
			int position = addressTranslation(address, offset);
			rawDeviceRW.seek(position);
			rawDeviceRW.write(dataToWrite, 0, dataToWrite.length);
		} catch (IOException e) {
		
			throw new DiskControllerException("Could not write block.");
		}
		
	}
	
	public byte[] rawReadBlock(int address, int offset) throws DiskControllerException{
		
		byte[] readBuffer = new byte[CONFIG.BLOCK_SIZE];
		int position = addressTranslation(address, offset);
		
		try {
			rawDeviceRW.seek(position);
			rawDeviceRW.read(readBuffer, 0 , readBuffer.length);
			return readBuffer;
		} catch (IOException e) {
			
			throw new DiskControllerException("Could not read Block.");
		}
		
		
	}
	
	public byte[] rawAddressRead(){
		
		return intAddressToBytes(0, 1);
	}
	
	public void rawAddressWrite(){
		
		//bytesToIntTranslation()
		
	}
	
	/**********************************
	 *    Translations and conversions
	 *********************************/
	
	private int addressTranslation(int address, int offset){
		
		int position = METADATA_LENGTH + (CONFIG.BLOCK_SIZE * address) + offset;
		
		return position;
	}
	
	public byte[] intAddressToBytes(int length, int intAddress){
		
		ByteBuffer dbuf = ByteBuffer.allocate(length);
		dbuf.putShort((short) intAddress);
		byte[] bytes = dbuf.array();
		return bytes; 
		
	}
	
	public int byteAddressToInt(byte[] byteAddress){
		
		ByteBuffer wrapped = ByteBuffer.wrap(byteAddress);
		short num = wrapped.getShort(); 
		
		return num;
	}
	
	/**********************************
	 *    Disk Directory Getters
	 *********************************/
	public DiskDirectory getDirectory() {
		return directory;
	}
	
		

}
