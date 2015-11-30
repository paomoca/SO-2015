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
			
			inKey = rawMetadataRead(systemKey.length, true);
			
			if(!Arrays.equals(systemKey, inKey)){
				
				//TODO: ESCRIBIR EL KEY AL PRINCIPIO HAY QUE HACER UN RESET DE LA METADATA A CERO OTRA VEZ PORQUE VAMOS A ESCRIBIR.
				NEW_DISK = true;
				metadataLengthRewind(systemKey.length);
				
				throw new DiskFormatException("This device is already initialized with a different FileSystem");
				
			}
			
			
			
		} catch (DiskControllerException e) {
			throw new DeviceInitializationException("An error occured trying to identify the device.");
		}
		
	}
	
	public void formatDevice() throws DeviceInitializationException, DiskFormatException {
		METADATA_LENGTH = 0;

		try {

			rawMetadataWrite(systemKey, systemKey.length, true);
			
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
			//falta read address del directorio
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
				byte[] freeSpaceManagerBlockDataSize = rawMetadataRead(CONFIG.FREE_SPACE_MANAGER_INITIAL_CONTROL_BYTE_SIZE,true);
				
				//We get an int from those bytes so we can know the actual number of blocks.
				int numberOfBlocksToRead = bytesToInt(freeSpaceManagerBlockDataSize);
				
				//TODO: FALTA MAS LOGICA
				// 2. Next we read that amount of blocks through a rawMetadataRead;
				byte[] freeSpaceManagerData = rawMetadataRead(CONFIG.BLOCK_SIZE*numberOfBlocksToRead,true);
				
				// 3. We use the data we read as an argument to initialize the DiskFreeSpaceManager
				DiskFreeSpaceManager.getInstance(freeSpaceManagerData);
				
			}

		
	}
	
	/**********************************
	 *    Raw Read-Write.
	 *    USED BY THE REST OF THE RAW FUNCTIONS. THIS ARE THE LOWEST LEVEL METHODS REACHED BEFORE GETTING THE ACTUAL DATA.
	 * @throws DiskControllerException 
	 *********************************/

	private void rawWrite(int position, byte[] dataToWrite, int length) throws DiskControllerException{

		if(CONFIG.DEBUG_SESSION){
			
			System.out.println("DEBUG SESSION: RAW WRITE"
					+ "\nPOSITION: "+position
					+ "\nDATA TO WRITE: "+ new String(dataToWrite)
					+ "\nDATA LENGTH: "+dataToWrite.length
					+ "\nUSER GIVEN LENGTH: "+length
					+ "\nFIN RAW WRITE LINEA 175");
		} else {
			try {
				//OFFSET is already considered within the position variable
				rawDeviceRW.seek(position);
				rawDeviceRW.write(dataToWrite, 0, length);
			} catch (IOException e) {
			
				throw new DiskControllerException("");
			}
		}	
		
	}
	
	private byte[] rawRead(int position, int length) throws DiskControllerException{
		
		
		if(CONFIG.DEBUG_SESSION){
			
			System.out.println("DEBUG SESSION: RAW READ"
					+ "\nPOSITION: "+position
					+ "\nUSER GIVEN LENGTH: "+length
					+ "\nFIN RAW WRITE LINEA 193");
			return new byte[1];
		} else {
			
			byte[] readBuffer = new byte[length];
			
			try {
				rawDeviceRW.seek(position);
				//OFFSET is already considered within the position variable
				rawDeviceRW.read(readBuffer, 0 , length);
				return readBuffer;
				
			} catch (IOException e) {
				
				throw new DiskControllerException("");
			}
			
		}
		
		
		
	}
	
	/*****************************************************************************************
	 * USED TO READ ENTIRE BLOCKS BASED ON BLOCK SIZE
	 *****************************************************************************************/
	
	public void rawWriteBlock(int address, byte[] dataToWrite, int length) throws DiskControllerException{
			
			if(length > CONFIG.BLOCK_SIZE){
				throw new DiskControllerException("An error occured trying to write a block, "
						+ "data[] size did not correspond to "+CONFIG.BLOCK_SIZE);
			}
			
			//We take no offset since we are reading the entire block.
			int position = addressTranslation(address);
			
			rawWrite(position, dataToWrite, length);
		
	}
	
	public byte[] rawReadBlock(int address) throws DiskControllerException{
		
		//We take no offset since we are reading the entire block.
		int position = addressTranslation(address);
		
		byte[] readBuffer = rawRead(position, CONFIG.BLOCK_SIZE);
		
		return readBuffer;
		
		
	}

	/*****************************************************************************************
	 * USED TO READ AND WRITE BLOCK CONTROL DATA ONLY
	 *****************************************************************************************/
	
	public void setDeduplicationCounter(int address, int counter) throws DiskControllerException, IncorrectLengthConversionException{
		
		int position = addressTranslation(address, CONFIG.DEDUPLICATION_CONTROL_OFFSET);
		byte[] byteCounter = intToBytes(CONFIG.DEDUPLICATION_CONTROL_SIZE, counter);
		
		//Would ideally write in address block position 0.
		rawWrite(position, byteCounter, CONFIG.DEDUPLICATION_CONTROL_SIZE);
		
	}
	
	public void setBlockAccessFrecuency(int address, int frequency) throws DiskControllerException, IncorrectLengthConversionException{
		
		int position = addressTranslation(address, CONFIG.FREQUENCY_CONTROL_OFFSET);
		byte[] byteFrequency = intToBytes(CONFIG.FREQUENCY_CONTROL_SIZE, address);
		
		//Would ideally write in address block position 2.
		rawWrite(position, byteFrequency, CONFIG.FREQUENCY_CONTROL_SIZE);
	}
	
	public int getDeduplicationCounter(int address) throws IncorrectLengthConversionException, DiskControllerException{
		
		int position = addressTranslation(address, CONFIG.DEDUPLICATION_CONTROL_OFFSET);
		byte[] byteCounter = rawRead(position, CONFIG.DEDUPLICATION_CONTROL_SIZE);
		
		return bytesToInt(byteCounter);
		
	}
	
	public int getBlockAccessFrecuency(int address) throws DiskControllerException, IncorrectLengthConversionException{
		
		int position = addressTranslation(address, CONFIG.FREQUENCY_CONTROL_OFFSET);
		byte[] byteFrequency = rawRead(position, CONFIG.FREQUENCY_CONTROL_SIZE);
		
		return bytesToInt(byteFrequency);
		
	}
	
	public void incrementDeduplicationCounter(int address) throws IncorrectLengthConversionException, DiskControllerException{
		
		//GETS THE CURRENT DEDUPLICATION CONTROL VALUE
		int currentValue = getDeduplicationCounter(address);
		currentValue += 1;
		//SETS NEW COUNTER VALUE
		setDeduplicationCounter(address, currentValue);
		
	}
	
	public void incrementBlockAccessFrecuency(int address) throws DiskControllerException, IncorrectLengthConversionException{
		
		//GETS THE CURRENT FREQUENCY CONTROL VALUE
		int currentValue = getBlockAccessFrecuency(address);
		currentValue += 1;
		//SETS NEW COUNTER VALUE
		setBlockAccessFrecuency(address, currentValue);
		
	}
	
	
	/*****************************************************************************************
	 * USED TO READ BLOCK PAYLOADS ONLY
	 *****************************************************************************************/
	
	public void rawWriteBlockPayload(int address, byte[] dataToWrite, int length) throws DiskControllerException{
			
			if(length > CONFIG.BLOCK_PAYLOAD_SIZE){
				throw new DiskControllerException("An error occured trying to write a block PAYLOAD, "
						+ "data[] size did not correspond to "+CONFIG.BLOCK_PAYLOAD_SIZE);
			}
			
			//We take as offset the amount of control bytes whenever we want to write te actual payload.
			int position = addressTranslation(address, CONFIG.CONTROL_BYTES_SIZE);
			
			rawWrite(position, dataToWrite, length);
		
	}
	
	public byte[] rawReadBlockPayload(int address) throws DiskControllerException{
		
		//We take the amount of control bytes as offset to translate address.
		int position = addressTranslation(address, CONFIG.CONTROL_BYTES_SIZE);
		
		byte[] readBuffer = rawRead(position, CONFIG.BLOCK_PAYLOAD_SIZE);
		
		return readBuffer;
		
		
	}
	
	/*****************************************************************************************
	 * USED WHENEVER WE ARE READING AND WRITING IDB INFORMATION. (USUALLY 4 BYTE ADDRESSES BUT
	 * THIS PARAMETER IS CONFIGURABLE WITHIN CONFIG.
	 *****************************************************************************************/
	
	public int rawAddressRead(int idbAddress, int idbOffset) throws IncorrectLengthConversionException, DiskControllerException{
		
		int position = IDBPositionTranslation(idbAddress, idbOffset);
		
		byte[] blockAddressReference = rawRead(position, CONFIG.ADDRESS_SIZE);
		return bytesToInt(blockAddressReference);
		
	}
	
	public void rawAddressWrite(int idbAddress, int idbOffset, int blockAddressReferenceToWrite) throws IncorrectLengthConversionException, DiskControllerException{
		
		byte[] byteBlockAddressReference = intToBytes(CONFIG.ADDRESS_SIZE, blockAddressReferenceToWrite);
		int position = IDBPositionTranslation(idbAddress, idbOffset);

		rawWrite(position, byteBlockAddressReference, CONFIG.ADDRESS_SIZE);
		
		
	}
	
	
	//TODO: TODAVIA NO ESTA BIEN
	/*****************************************************************************************
	 * METHODS IN CHARGE OF METADATA MANIPULATION (READ/WRITE).
	 * This class is important because it allows the amount of meta data content at the beginning
	 * of the disk to be dynamically updated. 
	 * IT IS IMPORTANT TO NOTE THAT WITHIN EACH CALL TO THIS FUNCTION THE METADATA LENGTH INCREASES.
	 *****************************************************************************************/
	
	private byte[] rawMetadataRead(int length, boolean increaseMetadataLength) throws DiskControllerException{
		
		byte metadata[] = rawRead(METADATA_LENGTH, length);
		
		if(increaseMetadataLength){
			METADATA_LENGTH += length;
		}
		
		
		return  metadata;
	}
	
	private void rawMetadataWrite(byte[] data, int length, boolean increaseMetadataLength) throws DiskControllerException{
		
		rawWrite(METADATA_LENGTH, data, length);
		
		if(increaseMetadataLength){
			METADATA_LENGTH += length;
		}
		
	}
	
	private void metadataLengthRewind(int length){
		METADATA_LENGTH -= length;
	}
	
	
	/**********************************
	 *  Translations and conversions
	 *********************************/
	
	/*FOR A GIVEN OFFSET*/
	private int addressTranslation(int address, int offset){
		
		int position = METADATA_LENGTH + (CONFIG.BLOCK_SIZE * address) + offset;
		
		return position;
	}
	
	/*OFFSET 0*/
	private int addressTranslation(int address){
		
		int position = METADATA_LENGTH + (CONFIG.BLOCK_SIZE * address);
		
		return position;
	}
	
	/* The offset given to this function is based on the actual address offset (there is no consideration for bytes),
	 * this function translates this position in terms of bytes taking into consideration the address size. */
	private int IDBPositionTranslation(int address, int offset){
		
		int position = METADATA_LENGTH + (CONFIG.BLOCK_SIZE * address) + (offset * CONFIG.ADDRESS_SIZE);
		
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
