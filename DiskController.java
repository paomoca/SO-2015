import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class DiskController {
	
	private int BLOCK_SIZE;
	private int CONTROL_SIZE;
	private int METADATA_LENGTH;
	
	byte[] systemKey = "0KYHNzQbaIRPe6APYx3NLg5kX3VsEvN52eOYAmJl7YlrahtGOI8xy22RrUUhMyMtPNYWAOEfes376IQIMmHPf1JUE8cwm26xBTN69pF9hCk6jc2f23mXNE2Z8CQLEU9RcJGGXO9Zb3vgzyHB2JhQs0zNf08DEPt6kEWgB0yrXpCcYoljprSVtjGh2rJfJOXmSR2oz8gSADU7y7nBPhznVGMKCKyeCfiUCQWIZ4BHCqq1ul4P1Nlcj9g1K8YorxCF3NAsMELBMlINU9oaNgc04nAX7VqmulOOhAUUCs4D2AV1yJ8aQSkjloBkwBp8StAWgpaP9fuuy4FBKGtHeTRwf4XNZqT3TyNre0PLP7usiijDo2RaXDARleKaEbsISYQBUBUYhrH3QtPV2qJ7bwtcj2uAvlUP4KSDSE5yAuGEoSXs69quHk3OR2aWarXhuvQ7kVeKincMNzo2UzR0XRuW5zq1K1vl0yWR4jXaGurzyjYG0JYRlPk5Ba2sDjHmSOakgjGPAD55OxRTc5ztglNmSXMjU8zrSEGa1slM5LjjxI9e1HUVD4yMc0QZBC6PtLI6snkWGq8fWHTm22q797O5vOyjWiQlDwee2RQM3vreKX1s85mMmgy6a3wzIREuH2ivwJjimp3KjUyUZe3h5tkhQtRnMSfCqw57IZ5IGbOt9b3to44Ghj5k3T8RUX43G0cheT81DYT1VpATlAgQlXCARjyBDEpIuiAB6x9Nfpg1b5SskvJ3oM7Kyl0AM9GTlZOLy1tNRtFejtgfwvZm7inWEFK1AGzftB2RsGlODbKaaNDErZaqYFKnxAkcsmJ0pQtQLo25fUYS9N577F8Hs6BrFc7axv4kkIbLUytFvNUAkIwpWsbTDJ4fCQz9VQXChO4Qsog8wxCILspfR5qb0QNV9a9V8zUYVnSKjBVCtPyyrZpXqz1WmhuYUaWa0XNyKR45wyN2mjXyfOlopxtwhK3BsXBs5jrnREuQMl83gpWxDHTzNYjOgiJCU6eYhAFxjBOl".getBytes();

	private RandomAccessFile rawDeviceRW;
	private DiskDirectory directory;
	private FreeSpaceManager fsm;
	
	public DiskController(String HDName, int blockSize, int controlSize) throws DiskControllerException{
	
		this.BLOCK_SIZE = blockSize;
		this.CONTROL_SIZE = controlSize;
		
		try {
			
			//The order is important please do not modify.
			mountDevice(HDName);
			deviceIdentification();
			directoryInitialization();
			freeSpaceManagerInitialization();
			
			
		} catch (DeviceInitializationException e) {
			
			throw new DiskControllerException(e.toString());
		}

	}
	
	/**********************************
	 *    Initialization Functions
	 *********************************/
	
	public void mountDevice(String HDName) throws DeviceInitializationException{
		
		//Reference to the raw device	
		try {
			rawDeviceRW = new RandomAccessFile(HDName, "rw");
		} catch (FileNotFoundException e) {
			
			throw new DeviceInitializationException("Unable to mount device.");
			
		}
	}
	
	public void deviceIdentification() throws DeviceInitializationException{
		
		//Initialize METADATA_LENGTH in 0 so we can start reading in the actual position 0. 
		//(Check addressTranslation function)
		METADATA_LENGTH = 0;
		byte[] inKey;
		
		try {
			
			inKey = rawRead(0, 0, systemKey.length);
			if(!Arrays.equals(systemKey, inKey)){
				throw new DeviceInitializationException("This device is already initialized with a different FileSystem.\n"
						+ "Please use a raw device.");
			}
			
			//If everything went well we add the size of the key to METADATA_LENGTH-1 because we start in 0.
			METADATA_LENGTH += systemKey.length-1;
			
		} catch (DiskControllerException e) {
			throw new DeviceInitializationException("An error occured trying to identify the device.");
		}
		
	}
	
	public void directoryInitialization(){
		
		
	}
	
	public void freeSpaceManagerInitialization() throws DiskControllerException{
	
		try {
			
			int numberOfBlocks = (int) ((rawDeviceRW.length()-METADATA_LENGTH)/BLOCK_SIZE);
			fsm = new FreeSpaceManager(numberOfBlocks);
			
		} catch (IOException e) {
			
			throw new DiskControllerException("Unable to initialize Free Space Manager.");
		}
		
		
		
	}
	
	/**********************************
	 *    Raw Read-Write
	 *********************************/
	
	//Function used whenever we need to write to disk. This is the only function that actually writes into the raw device.
	private void rawWrite(int address, int offset, byte[] dataToWrite) throws DiskControllerException{
		
		try {
			int position = addressTranslation(address, offset);
			rawDeviceRW.seek(position);
			rawDeviceRW.write(dataToWrite, 0, dataToWrite.length);
		} catch (IOException e) {
		
			throw new DiskControllerException("");
		}
		
	}
	
	private byte[] rawRead(int address, int offset, int length) throws DiskControllerException{
		
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
	
	private int addressTranslation(int address, int offset){
		
		int position = METADATA_LENGTH + (BLOCK_SIZE * address) + offset;
		
		return position;
	}
	
	/**********************************
	 *    Disk Directory Getters
	 *********************************/
	public DiskDirectory getDirectory() {
		return directory;
	}
	
	

}
