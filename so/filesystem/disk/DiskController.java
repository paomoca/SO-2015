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
			
			//Initialize METADATA_LENGTH in 0 so we can start reading in the actual position 0. 
			//(Check addressTranslation function to see why this is important)
			METADATA_LENGTH = 0;
			
			/**** The order is important please do not modify. ******/
			mountDevice();
			if(formatFlag){
				formatDevice();
			}else{
				deviceIdentification();
			}
	
			freeSpaceManagerInitialization();
			directoryInitialization();
			
			
		} catch (DeviceInitializationException e) {
			
			throw new DiskControllerException(e.toString());
		} catch (IncorrectLengthConversionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		
		byte[] inKey;
		
		try {
			
			inKey = rawMetadataRead(systemKey.length);
			
			if(!Arrays.equals(systemKey, inKey)){
				
				//TODO: ESCRIBIR EL KEY AL PRINCIPIO HAY QUE HACER UN RESET DE LA METADATA A CERO OTRA VEZ PORQUE VAMOS A ESCRIBIR.
				NEW_DISK = true;
				
				throw new DiskFormatException("This device is already initialized with a different FileSystem");
				
			}
			
			
			
		} catch (DiskControllerException e) {
			throw new DeviceInitializationException("An error occured trying to identify the device.");
		}
		
	}
	
	public void formatDevice() throws DeviceInitializationException, DiskFormatException {
		METADATA_LENGTH = 0;

		try {

			rawMetadataWrite(systemKey);
			
			NEW_DISK = true;


		} catch (DiskControllerException e) {
			throw new DiskFormatException(
					"An error occured trying formatting the device.");
		}
	}
	
	private void directoryInitialization() throws IncorrectLengthConversionException, DiskControllerException{
		
		if(NEW_DISK){
			//TODO: Uses free space manager to allocate a block for the Directory Inode?
		} else {
			//TODO: gets the directory inode 4 byte address
			int directoryAddress = rawAddressRead(METADATA_LENGTH, 0);
		}
		
		
		
		
	}
	
	private void freeSpaceManagerInitialization() throws DiskControllerException, IncorrectLengthConversionException, IOException{
			
			if(NEW_DISK){
				//If a new disk is being used the Disk Free Space Manager is initialized with the total Disk Available Space in int.
				int numberOfBlocks = (int) ((rawDeviceRW.length()-METADATA_LENGTH)/CONFIG.BLOCK_SIZE);

				//Disk Free Space Manager is initialized but never used within this class.
				DiskFreeSpaceManager.getInstance(numberOfBlocks);
				
			} else {
				
				/* If it is not a new disk and the disk already contains free space information,
				 * the Disk Free Space Manager is initialized with with that information. */
				
				// 1. First we get byte[] information about how many blocks contain the Free Space Manager data.
				byte[] freeSpaceManagerBlockDataSize = rawMetadataRead(CONFIG.FREE_SPACE_MANAGER_INITIAL_CONTROL_BYTE_SIZE);
				
				//We get an int from those bytes so we can know the actual number of blocks.
				int numberOfBlocksToRead = bytesToInt(freeSpaceManagerBlockDataSize);
				
				//TODO: FALTA MAS LOGICA
				// 2. Next we read that amount of blocks through a rawMetadataRead;
				byte[] freeSpaceManagerData = rawMetadataRead(CONFIG.BLOCK_SIZE*numberOfBlocksToRead);
				
				// 3. We use the data we read as an argument to initialize the DiskFreeSpaceManager
				DiskFreeSpaceManager.getInstance(freeSpaceManagerData);
				
			}

		
	}
	
	/**********************************
	 *    Raw Read-Write.
	 *    USED BY THE REST OF THE RAW FUNCTIONS. THIS ARE THE LOWEST LEVEL METHODS REACHED BEFORE GETTING THE ACTUAL DATA.
	 *********************************/
	
	//Function used whenever we need to write to disk. This is the only function that actually writes into the raw device.
	public void rawWrite(int address, int offset, byte[] dataToWrite, int length) throws DiskControllerException{
		
		try {
			int position = addressTranslation(address, offset);
			rawDeviceRW.seek(position);
			rawDeviceRW.write(dataToWrite, 0, length);
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
	
	public void rawWriteBlockPayload(int address, byte[] dataToWrite, int length) throws DiskControllerException{
			
			if(dataToWrite.length > CONFIG.BLOCK_PAYLOAD_SIZE){
				throw new DiskControllerException("An error occured trying to write a block, "
						+ "data[] size did not correspond to "+CONFIG.BLOCK_PAYLOAD_SIZE);
			}
			
			rawWrite(address, CONFIG.CONTROL_BYTES_SIZE, dataToWrite, length);
		
	}
	
	public byte[] rawReadBlockPayload(int address) throws DiskControllerException{
		
		byte[] readBuffer = rawRead(address, CONFIG.CONTROL_BYTES_SIZE, CONFIG.BLOCK_PAYLOAD_SIZE);
		
		return readBuffer;
		
		
	}
	
	
	//TODO: PARA LEER Y ESCRIBIR DIRECCIONES
	/*****************************************************************************************
	 * USED WHENEVER WE ARE READING AND WRITING IDB INFORMATION. (USUALLY 4 BYTE ADDRESSES)
	 *****************************************************************************************/
	
	public int rawAddressRead(int idbAddress, int offset) throws IncorrectLengthConversionException, DiskControllerException{
		
		byte[] blockAddressReference = rawRead(idbAddress, offset, CONFIG.ADDRESS_SIZE);
		return bytesToInt(blockAddressReference);
		
	}
	
	public void rawAddressWrite(int idbAddress, int offset, int blockAddressReference) throws IncorrectLengthConversionException, DiskControllerException{
		
		byte[] byteBlockAddressReference = intToBytes(CONFIG.ADDRESS_SIZE, blockAddressReference);
		rawWrite(idbAddress, offset, byteBlockAddressReference, CONFIG.ADDRESS_SIZE);
		
		
	}
	
	
	/*****************************************************************************************
	 * METHODS IN CHARGE OF METADATA MANIPULATION (READ/WRITE).
	 * This class is important because it allows the amount of meta data content at the beginning
	 * of the disk to be dynamically updated. 
	 *****************************************************************************************/
	
	public byte[] rawMetadataRead(int length) throws DiskControllerException{
		
		byte metadata[] = rawRead(0,0,length);
		METADATA_LENGTH += metadata.length;
		
		return  metadata;
	}
	
	public void rawMetadataWrite(byte[] data) throws DiskControllerException{
		
		rawWrite(0, 0, data, data.length);
		METADATA_LENGTH+=data.length;
		
	}
	
	/**********************************
	 *  Translations and conversions
	 *********************************/
	
	private int addressTranslation(int address, int offset){
		
		int position = METADATA_LENGTH + (CONFIG.BLOCK_SIZE * address) + offset;
		
		return position;
	}
	
	private byte[] intToBytes(int length, int intAddress) throws IncorrectLengthConversionException{
		
		if (length == 2) {
			return ByteBuffer.allocate(2).putShort((short) intAddress).array(); 
		} else if (length == 4) {
			return ByteBuffer.allocate(4).putInt(intAddress).array();
		} else {
			throw new IncorrectLengthConversionException("ERROR: The number you are trying to convert is neither 2 nor 4 bytes long.");
		}
		
	}
	
	private int bytesToInt(byte[] byteAddress) throws IncorrectLengthConversionException{
		
		if (byteAddress.length == 2) {
			return ByteBuffer.wrap(byteAddress).getShort();
		} else if(byteAddress.length == 4) {
			return ByteBuffer.wrap(byteAddress).getInt();
		} else {
			throw new IncorrectLengthConversionException("ERROR: The bytes array you are trying to convert is neither 2 nor 4 bytes long.");
		}	
		
	}
	
	/**********************************
	 *    Disk Directory Manipulation
	 *********************************/
	public DiskDirectory getDirectory() {
		return directory;
	}
	
	public void newDirectoryEntry(String fileName, int inodeAddress){
		
		//TODO: WRITE TO DISK FILE CONTAINING THE DIRECTORY.
		
		directory.newFile(fileName, inodeAddress);
		
	}
	
		

}
