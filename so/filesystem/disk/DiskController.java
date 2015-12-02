package so.filesystem.disk;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

import so.filesystem.filemanagment.InodeDirectPointerIndexOutOfRange;
import so.filesystem.general.CONFIG;


//TODO: metadata new disk initialization en secuencia
//TODO: metadata known disk initialization

public class DiskController {
	
	private static DiskController self = null;
	
	//Initialize METADATA_LENGTH in 0 so we can start reading in the actual position 0. 
	public int METADATA_LENGTH=0;
	private byte[] METADATA_DISK_KEY;
	private int METADATA_DIRECTORY_ADDRESS;
	private int METADATA_NUMBER_DISK_FSM_BLOCKS;
	
	private boolean NEW_DISK = false;
	
	private RandomAccessFile rawDeviceRW;
	private DiskDirectory directory;

	
	private DiskController(boolean formatFlag) throws DiskControllerException, DiskFormatException, UnidentifiedMetadataTypeException, IOException{

		try {
			
			//(Check addressTranslation function to see why this is important)
			
			/***** The order is important please do not modify. ******/
			mountDevice();
			if(formatFlag){
				metadataNewDiskInitialization();
			}else{
				deviceIdentification();
				metadataKnownDiskInitialization();
			}
	
			//directoryInitialization();
			
			
		} catch (DeviceInitializationException e) {
			
			throw new DiskControllerException(e.toString());
		} catch (IncorrectLengthConversionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	
	public static DiskController getInstance(boolean formatFlag) throws DiskControllerException, DiskFormatException, UnidentifiedMetadataTypeException, IOException {
		if (self == null) {
			self = new DiskController(formatFlag);
		}
		return self;
	}
	
	public static DiskController getInstance() throws DiskControllerException{
		if (self == null) {
			throw new DiskControllerException("ERROR: DiskController not yet init.");
		}
		return self;
	}
	
	/**********************************
	 *    Initialization Functions
	 *********************************/
	
	private void mountDevice() throws DeviceInitializationException{
		
		//Reference to the raw device	
		try {
			rawDeviceRW = new RandomAccessFile(CONFIG.DISK_LOCATION, "rw");
			if(CONFIG.DEBUG_SESSION){
				System.out.println("Device "+CONFIG.DISK_LOCATION+" mounted successfully.");
				System.out.println("Length"+rawDeviceRW.length());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new DeviceInitializationException("Unable to mount device.");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//TODO: AQUI ESTAN LAS FORMAS DE LEER Y ESCRIBIR LA METADATA NO SE DONDE VAN, DEPENDE DE LA INICIALIZACION DE JUAN
	private void metadataNewDiskInitialization() throws UnidentifiedMetadataTypeException, DiskControllerException, IncorrectLengthConversionException, IOException{
		
		//Saves the key at the beginning of the disk.
		METADATA_DISK_KEY = CONFIG.DISK_SYSTEMKEY;
		rawMetadataWrite(METADATA_DISK_KEY, "DISK_KEY");
		 
		//TODO: COMO COMENZAR EL DIRECTORIO
		//Directory Inode Address is always cero.
		METADATA_DIRECTORY_ADDRESS = 0;
		rawMetadataWrite(Integer.valueOf(METADATA_DIRECTORY_ADDRESS), "DIRECTORY_ADDRESS");
		//TODO:
		//Create directory Inode and write it on the corresponding block?.
			 
		//Initializes the DiskFreeSpaceManager with the number of blocks remaining after the meta data bytes.
		//Final meta data length is dependent on the result of this initialization.
		long byteSize = (long) 22655952486.0;
		long numberOfBlocks = ((byteSize - new Long(CONFIG.INITIAL_METADATA_SIZE))/ new Long(CONFIG.BLOCK_SIZE));
		DiskFreeSpaceManager.getInstance((int) numberOfBlocks);
		METADATA_NUMBER_DISK_FSM_BLOCKS  = DiskFreeSpaceManager.getInstance().getBitMapSizeInBlocks();
		rawMetadataWrite(Integer.valueOf(METADATA_NUMBER_DISK_FSM_BLOCKS), "FSM_NUMBER_OF_BLOCKS");
		METADATA_LENGTH = CONFIG.INITIAL_METADATA_SIZE + (METADATA_NUMBER_DISK_FSM_BLOCKS * CONFIG.BLOCK_SIZE);
		
		byte[] disk_fsm_bitmap = DiskFreeSpaceManager.getInstance().updateFreeSpace();
		rawMetadataWrite(disk_fsm_bitmap, "FSM_BITMAP");
		
	}
	
	//TODO: AQUI ESTAN LAS FORMAS DE LEER Y ESCRIBIR LA METADATA NO SE DONDE VAN, DEPENDE DE LA INICIALIZACION DE JUAN
	private void metadataKnownDiskInitialization()  throws UnidentifiedMetadataTypeException, DiskControllerException, IncorrectLengthConversionException{
		
		//How to read each meta data value
		
		METADATA_DISK_KEY = (byte[]) rawMetadataRead("DISK_KEY");
		
		METADATA_DIRECTORY_ADDRESS = (int) rawMetadataRead("DIRECTORY_ADDRESS");
		
		//Final meta data length is dependent on the result of this read.
		METADATA_NUMBER_DISK_FSM_BLOCKS = (int) rawMetadataRead("FSM_NUMBER_OF_BLOCKS");
		METADATA_LENGTH = CONFIG.INITIAL_METADATA_SIZE + (METADATA_NUMBER_DISK_FSM_BLOCKS * CONFIG.BLOCK_SIZE);
		
		byte[] fsmBitmap = (byte[]) rawMetadataRead("FSM_BITMAP");
		DiskFreeSpaceManager.getInstance(fsmBitmap);
		
	}
	
	private void deviceIdentification() throws DeviceInitializationException, DiskFormatException, UnidentifiedMetadataTypeException, DiskControllerException, IncorrectLengthConversionException{
		
		byte[] inKey = (byte[]) rawMetadataRead("DISK_KEY");
		
		//try {
			
			if(CONFIG.DEBUG_SESSION){
				System.out.println("INKEY: "+new String(inKey));
			}
			if(!Arrays.equals(CONFIG.DISK_SYSTEMKEY, inKey)){
				
				//TODO: ESCRIBIR EL KEY AL PRINCIPIO HAY QUE HACER UN RESET DE LA METADATA A CERO OTRA VEZ PORQUE VAMOS A ESCRIBIR.
				NEW_DISK = true;
				
				throw new DiskFormatException("This device is already initialized with a different FileSystem");
				
			}
				
//		} catch (DiskControllerException e) {
//			throw new DeviceInitializationException("An error occured trying to identify the device.");
//		}
	}
	
	
	private void directoryInitialization() throws IncorrectLengthConversionException, DiskControllerException{
		
		if(NEW_DISK){
			//TODO: Uses free space manager to allocate a block for the Directory Inode?
		} else {
			//TODO: gets the directory inode 4 byte address
			//falta read address del directorio
		}
	
	}
	
	private void freeSpaceManagerInitialization(){
			
			
		
	}
	
	/**********************************
	 *    Raw Read-Write.
	 *    USED BY THE REST OF THE RAW FUNCTIONS. THIS ARE THE LOWEST LEVEL METHODS REACHED BEFORE GETTING THE ACTUAL DATA.
	 * @throws DiskControllerException 
	 *********************************/

	private void rawWrite(int position, byte[] dataToWrite, int length) throws DiskControllerException{

		if(CONFIG.DEBUG_SESSION){
			
			System.out.println("\n\n-------------DEBUG SESSION: RAW WRITE"
					+ "\nPOSITION: "+position
					+ "\nDATA TO WRITE String: "+ new String(dataToWrite)
					+ "\nDATA LENGTH: "+dataToWrite.length
					+ "\nUSER GIVEN LENGTH: "+length);
			
	
				try {
					System.out.println("\nDATA TO WRITE INT: "+bytesToInt(dataToWrite)+"---------");
					byte[] test = rawRead(position, length);
					System.out.println("\nCOMPROBACION READ STRING: "+new String(test)+"---------------------");
					System.out.println("\nCOMPROBACION READ INT: "+bytesToInt(test)+"---------------------\n\n");
				} catch (IncorrectLengthConversionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			
		} 
			try {
				//OFFSET is already considered within the position variable
				rawDeviceRW.seek(position);
				rawDeviceRW.write(dataToWrite, 0, length);
			} catch (IOException e) {
			
				throw new DiskControllerException("");
			}
		
		
	}
	
	private byte[] rawRead(int position, int length) throws DiskControllerException{
		
			byte[] readBuffer = new byte[length];
			
			try {
				rawDeviceRW.seek(position);
				//OFFSET is already considered within the position variable
				rawDeviceRW.read(readBuffer, 0 , length);
				
				
				if(CONFIG.DEBUG_SESSION){
					
					System.out.println("\n\n***********DEBUG SESSION: RAW READ"
							+ "\nPOSITION: "+position
							+ "\nUSER GIVEN LENGTH: "+length
							+"\nDATA READ: "+new String(readBuffer));
					
					try {
						System.out.println("\nDATA READ INT: "+bytesToInt(readBuffer)+"***********\n\n");
					} catch (IncorrectLengthConversionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
				
				
				return readBuffer;
				
			} catch (IOException e) {
				
				throw new DiskControllerException("");
			}
		
	}
	
	/*****************************************************************************************
	 * USED TO GET AND SET THE INODE DIRECT, SINGLE INDIRECT AND DOUBLE INDIRECT POINTERS.
	 * @throws IncorrectLengthConversionException 
	 * @throws DiskControllerException 
	 * @throws InodeDirectPointerIndexOutOfRange 
	 *****************************************************************************************/
	
	public void writeFileSize(int inodeAddress, int fileSizeInTermsOfBytes) throws IncorrectLengthConversionException, DiskControllerException{
		
		int position = addressTranslation(inodeAddress);
		byte[] byteFileSize = intToBytes(CONFIG.INODE_INFO_FILE_SIZE_IN_BYTES, fileSizeInTermsOfBytes);
		
		rawWrite(position, byteFileSize, CONFIG.INODE_INFO_FILE_SIZE_IN_BYTES);
	
	}
	
	public int readFileSize(int inodeAddress) throws IncorrectLengthConversionException, DiskControllerException{
		
		int position = addressTranslation(inodeAddress);
		byte[] fileSize = rawRead(position, CONFIG.INODE_INFO_FILE_SIZE_IN_BYTES);
	
		return bytesToInt(fileSize);
	}
	
	public void writeDirectPointer(int inodeAddress, int inodeOffset, int pointToAddress) throws DiskControllerException, IncorrectLengthConversionException, InodeDirectPointerIndexOutOfRange{
		
		int position = directPointerAddressTranslation(inodeAddress, inodeOffset);
		byte[] bytePointToAddress = intToBytes(CONFIG.ADDRESS_SIZE, pointToAddress);
		
		if(CONFIG.DEBUG_SESSION){
			System.out.println("Request write Direct Pointer Inode Address: "+inodeAddress
					+"\noffset: "+inodeOffset
					+"\nposition: "+position
					+"wrote address: "+pointToAddress);
		}
		
		rawWrite(position, bytePointToAddress, CONFIG.ADDRESS_SIZE);
	}
	
	public int readDirectPointer(int inodeAddress, int inodeOffset) throws IncorrectLengthConversionException, DiskControllerException, InodeDirectPointerIndexOutOfRange{
		
		
		
		int position = directPointerAddressTranslation(inodeAddress, inodeOffset);
		byte[] directPointer = rawRead(position, CONFIG.ADDRESS_SIZE);
		
		if(CONFIG.DEBUG_SESSION){
			System.out.println("Read Direct Pointer Inode Address: "+inodeAddress
					+"\noffset: "+inodeOffset
					+"\nposition: "+position);
		}
		
		return bytesToInt(directPointer);
		
		
	
	}
	
	public void writeIDB1(int inodeAddress, int idb1Address) throws IncorrectLengthConversionException, DiskControllerException{
		
		int position = addressTranslation(inodeAddress, CONFIG.IDB1_OFFSET);
		byte[] byteIDB1Address = intToBytes(CONFIG.ADDRESS_SIZE, idb1Address);
		rawWrite(position, byteIDB1Address, CONFIG.ADDRESS_SIZE);
		
	}
	
	public int readIDB1(int inodeAddress) throws DiskControllerException, IncorrectLengthConversionException{
		
		int position = addressTranslation(inodeAddress, CONFIG.IDB1_OFFSET);
		byte[] byteIDB1 = rawRead(position, CONFIG.ADDRESS_SIZE);
		
		return bytesToInt(byteIDB1);
	}
	
	public void writeIDB2(int inodeAddress, int idb2address) throws IncorrectLengthConversionException, DiskControllerException{
		
		int position = addressTranslation(inodeAddress, CONFIG.IDB2_OFFSET);
		byte[] byteIDB2Address = intToBytes(CONFIG.ADDRESS_SIZE, idb2address);
		rawWrite(position, byteIDB2Address, CONFIG.ADDRESS_SIZE);
		
	}
	
	public int readIDB2(int inodeAddress) throws DiskControllerException, IncorrectLengthConversionException{
	
		int position = addressTranslation(inodeAddress, CONFIG.IDB2_OFFSET);
		byte[] byteIDB2 = rawRead(position, CONFIG.ADDRESS_SIZE);
		
		return bytesToInt(byteIDB2);
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
	
	public void setBlockAccessFrequency(int address, int frequency) throws DiskControllerException, IncorrectLengthConversionException{
		
		int position = addressTranslation(address, CONFIG.FREQUENCY_CONTROL_OFFSET);
		byte[] byteFrequency = intToBytes(CONFIG.FREQUENCY_CONTROL_SIZE, frequency);
		
		//Would ideally write in address block position 2.
		rawWrite(position, byteFrequency, CONFIG.FREQUENCY_CONTROL_SIZE);
	}
	
	public int getDeduplicationCounter(int address) throws IncorrectLengthConversionException, DiskControllerException{
		
		int position = addressTranslation(address, CONFIG.DEDUPLICATION_CONTROL_OFFSET);
		byte[] byteCounter = rawRead(position, CONFIG.DEDUPLICATION_CONTROL_SIZE);
		
		return bytesToInt(byteCounter);
		
	}
	
	public int getBlockAccessFrequency(int address) throws DiskControllerException, IncorrectLengthConversionException{
		
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
	
	public void incrementBlockAccessFrequency(int address) throws DiskControllerException, IncorrectLengthConversionException{
		
		//GETS THE CURRENT FREQUENCY CONTROL VALUE
		int currentValue = getBlockAccessFrequency(address);
		currentValue += 1;
		//SETS NEW COUNTER VALUE
		setBlockAccessFrequency(address, currentValue);
		
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
	
	public byte[] rawReadBlockPayload(int address, int length) throws DiskControllerException{
		
		//We take the amount of control bytes as offset to translate address.
		int position = addressTranslation(address, CONFIG.CONTROL_BYTES_SIZE);
		
		byte[] readBuffer = rawRead(position, length);
		
		return readBuffer;
			
	}
	
	
	/*****************************************************************************************
	 * USED WHENEVER WE ARE READING AND WRITING IDB INFORMATION. (USUALLY 4 BYTE ADDRESSES BUT
	 * THIS PARAMETER IS CONFIGURABLE WITHIN CONFIG.
	 *****************************************************************************************/
	
	public int rawAddressRead(int idbAddress, int idbOffset) throws IncorrectLengthConversionException, DiskControllerException{
		
		int position = IDBPositionTranslation(idbAddress, idbOffset);
		
		byte[] blockAddressReference = rawRead(position, CONFIG.ADDRESS_SIZE);
		
		if(CONFIG.DEBUG_SESSION){
			System.out.println("\nREAD ADDRESS"
			+"\nIDBBLOCK: "+idbAddress
			+"\nIDBOFFSET: "+idbOffset
			+"\nTRANSLATED POSITION: "+position);
		}
		
		return bytesToInt(blockAddressReference);
		
	}
	
	public void rawAddressWrite(int idbAddress, int idbOffset, int blockAddressReferenceToWrite) throws IncorrectLengthConversionException, DiskControllerException{
		
		byte[] byteBlockAddressReference = intToBytes(CONFIG.ADDRESS_SIZE, blockAddressReferenceToWrite);
		int position = IDBPositionTranslation(idbAddress, idbOffset);
		
		if(CONFIG.DEBUG_SESSION){
			System.out.println("\nADDRESS: "+bytesToInt(byteBlockAddressReference)
			+"\nIDBBLOCK: "+idbAddress
			+"\nIDBOFFSET: "+idbOffset
			+"\nTRANSLATED POSITION: "+position);
		}

		rawWrite(position, byteBlockAddressReference, CONFIG.ADDRESS_SIZE);
		
	}
	
	/*****************************************************************************************
	 * METHODS IN CHARGE OF METADATA MANIPULATION (READ/WRITE).
	 * This class is important because it allows the amount of meta data content at the beginning
	 * of the disk to be dynamically updated. 
	 * IT IS IMPORTANT TO NOTE THAT WITHIN EACH CALL TO THIS FUNCTION THE METADATA LENGTH INCREASES.
	 * @throws UnidentifiedMetadataTypeException 
	 * @throws DiskControllerException 
	 * @throws IncorrectLengthConversionException 
	 *****************************************************************************************/
	
	private Object rawMetadataRead(String type) throws UnidentifiedMetadataTypeException, DiskControllerException, IncorrectLengthConversionException{
		
		switch (type){
		
			case "DISK_KEY":
				
				byte[] diskKey = rawRead(CONFIG.METADATA_DISKSYSTEMKEY_OFFSET, CONFIG.METADATA_DISKSYSTEMKEY_SIZE);
				return diskKey;

			case "DIRECTORY_ADDRESS":
				
				byte[] dirAddr = rawRead(CONFIG.METADATA_DIRECTORY_ADDRESS_REFERENCE_OFFSET, CONFIG.METADATA_DIRECTORY_ADDRESS_REFERENCE_SIZE);
				return new Integer(bytesToInt(dirAddr));
				
			case "FSM_NUMBER_OF_BLOCKS":
				
				byte[] fsmNumberBlocks = rawRead(CONFIG.METADATA_DISKFSM_INITIAL_CONTROL_BYTE_OFFSET, CONFIG.METADATA_DISKFSM_INITIAL_CONTROL_BYTE_SIZE);
				return new Integer(bytesToInt(fsmNumberBlocks));
				
			case "FSM_BITMAP":
				byte[] fsmBitmap = rawRead(CONFIG.INITIAL_METADATA_SIZE, (METADATA_NUMBER_DISK_FSM_BLOCKS*CONFIG.BLOCK_PAYLOAD_SIZE));
				return fsmBitmap;
				
				
			default:
				throw new UnidentifiedMetadataTypeException("Unidentified requested metadata type.");
		}
		
	}

	private void rawMetadataWrite(Object data, String type) throws UnidentifiedMetadataTypeException, DiskControllerException, IncorrectLengthConversionException{
		
		switch (type){
		
			case "DISK_KEY":
				
				byte[] diskKey = (byte[]) data;
				rawWrite(CONFIG.METADATA_DISKSYSTEMKEY_OFFSET, diskKey ,CONFIG.METADATA_DISKSYSTEMKEY_SIZE);
				break;
			
			case "DIRECTORY_ADDRESS":
				
				
				byte[] dirAddr = intToBytes(CONFIG.METADATA_DIRECTORY_ADDRESS_REFERENCE_SIZE,(Integer) data);				
				rawWrite(CONFIG.METADATA_DIRECTORY_ADDRESS_REFERENCE_OFFSET, dirAddr ,CONFIG.METADATA_DIRECTORY_ADDRESS_REFERENCE_SIZE);
				break;
	
			case "FSM_NUMBER_OF_BLOCKS":
				
				byte[] fsmNumberOfBlocks = intToBytes(CONFIG.METADATA_DISKFSM_INITIAL_CONTROL_BYTE_SIZE,(Integer) data);
				rawWrite(CONFIG.METADATA_DISKFSM_INITIAL_CONTROL_BYTE_OFFSET,fsmNumberOfBlocks,CONFIG.METADATA_DISKFSM_INITIAL_CONTROL_BYTE_SIZE);
				break;
				
			case "FSM_BITMAP":
				byte[] bitMap = (byte[]) data;
				rawWrite(CONFIG.INITIAL_METADATA_SIZE, bitMap, (METADATA_NUMBER_DISK_FSM_BLOCKS*CONFIG.BLOCK_SIZE));
				break;
				
			default:
				throw new UnidentifiedMetadataTypeException("Unidentified requested metadata type.");
		}
		
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
		
		int position = METADATA_LENGTH + (CONFIG.BLOCK_SIZE * address) + offset;
		
		return position;
	}
	
	private int directPointerAddressTranslation(int inodeAddress, int directPointerOffset) throws InodeDirectPointerIndexOutOfRange{
		
		if(directPointerOffset > CONFIG.DIRECT_POINTERS-1){
			throw new InodeDirectPointerIndexOutOfRange("Direct pointer index out of range. Inode: "+inodeAddress+" Offset: "+directPointerOffset);
		}
		
		//Offset is handled in a per/pointer fashion. Address size must be taken into consideration during translation.
		int position = METADATA_LENGTH+(CONFIG.BLOCK_SIZE*inodeAddress)+CONFIG.INODE_INFO_MAX_SIZE+(directPointerOffset*CONFIG.ADDRESS_SIZE);
		
		if(CONFIG.DEBUG_SESSION){
			
			System.out.println("Translated DIRECT POINTER FROM ADDRESS: "+inodeAddress+" OFFSET: "+directPointerOffset+" TO: "+position);
		}
		
		return position;
	}
	
	public byte[] intToBytes(int length, int intAddress) throws IncorrectLengthConversionException{
		
		if (length == 2) {
			return ByteBuffer.allocate(2).putShort((short) intAddress).array(); 
		} else if (length == 4) {
			return ByteBuffer.allocate(4).putInt(intAddress).array();
		} else {
			throw new IncorrectLengthConversionException("ERROR: The number you are trying to convert is neither 2 nor 4 bytes long.");
		}
		
	}
	
	public int bytesToInt(byte[] byteAddress) throws IncorrectLengthConversionException{
		
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
	
	//TODO: mejorar exception
	public void finalize(){
		try {
			rawDeviceRW.close();
			System.out.println("Finalized.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not finalize.");
		}
	}
	
		

}
