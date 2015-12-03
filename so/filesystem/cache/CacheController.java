package so.filesystem.cache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map.Entry;

import so.filesystem.disk.DeviceInitializationException;
import so.filesystem.disk.DiskControllerException;
import so.filesystem.general.CONFIG;
import so.filesystem.general.FreeSpaceManager;

import java.util.*;

public class CacheController {
	
	private static CacheController self = null;

	private int METADATA_LENGTH;
	private long cacheHits;
	private Entry<Integer, Integer> lowestEntry;

	private RandomAccessFile rawDeviceRW;
	private CacheFreeSpaceManager fsm;
	private HashMap<Integer, Integer> diskVSCache;
	private HashMap<Integer, Integer> diskVSFrequency;

	private CacheController(boolean formatFlag)
			throws CacheControllerException, CacheFormatException {

		diskVSCache = new HashMap<Integer, Integer>();
		diskVSFrequency = new HashMap<Integer, Integer>();

		this.cacheHits = 0;
		
		TreeMap<Integer, Integer> dummy = new TreeMap<Integer, Integer>();
		dummy.put(-1, 65537);
		lowestEntry = dummy.lastEntry();

		cacheHits = 0;

		try {

			mountDevice();
			if(formatFlag){
				formatDevice();
			}else{
				deviceIdentification();
			}
			freeSpaceManagerInitialization();

		} catch (DeviceInitializationException e) {

			throw new CacheControllerException(e.toString());
		}
	}
	
	public static CacheController getInstance(boolean formatFlag) throws CacheControllerException, CacheFormatException {
		if (self == null) {
			self = new CacheController(formatFlag);
		}
		return self;
	}
	
	public static CacheController getInstance() throws CacheControllerException {
		if (self == null) {
			throw new CacheControllerException("ERROR: Cache Controller not yet init.");
		}
		return self;
	}



	public void deviceIdentification() throws DeviceInitializationException, CacheFormatException {

		// Initialize METADATA_LENGTH in 0 so we can start reading in the actual
		// position 0.
		// (Check addressTranslation function)
		METADATA_LENGTH = 0;
		byte[] inKey;

		try {

			inKey = rawRead(0, 0, CONFIG.CACHE_SYSTEMKEY.length);
			if (!Arrays.equals(CONFIG.CACHE_SYSTEMKEY, inKey)) {
				throw new CacheFormatException(
						"This device is already initialized with a different FileSystem.");
			}

			// If everything went well we add the size of the key to
			// METADATA_LENGTH.
			METADATA_LENGTH += CONFIG.CACHE_SYSTEMKEY.length;

		} catch (CacheControllerException e) {
			throw new DeviceInitializationException(
					"An error occured trying to identify the device.");
		}
	}

	public void mountDevice()
			throws DeviceInitializationException {

		// Reference to the raw device
		try {
			rawDeviceRW = new RandomAccessFile(CONFIG.CACHE_LOCATION, "rw");
		} catch (FileNotFoundException e) {

			throw new DeviceInitializationException("Unable to mount device.");

		}
	}

	public void formatDevice() throws DeviceInitializationException, CacheFormatException {
		METADATA_LENGTH = 0;

		try {

			rawWrite(0, 0, CONFIG.CACHE_SYSTEMKEY);

			// If everything went well we add the size of the key to
			// METADATA_LENGTH.
			METADATA_LENGTH += CONFIG.CACHE_SYSTEMKEY.length;

		} catch (CacheControllerException e) {
			throw new CacheFormatException(
					"An error occured trying formatting the device.");
		}
	}

	public void freeSpaceManagerInitialization()
			throws CacheControllerException {

		long byteSize = (long) 2147483648.0;
		long numberOfBlocks = ((byteSize / new Long(CONFIG.BLOCK_SIZE)));

		//int numberOfBlocks = (int) ((rawDeviceRW.length() - CONFIG.CACHE_SYSTEMKEY.length) / CONFIG.BLOCK_SIZE);
		System.out.println("bblocks av: " + numberOfBlocks);
		fsm = CacheFreeSpaceManager.getInstance((int) numberOfBlocks);
	}

	public void writeCacheBlock(int hddAddr, byte[] block, int frec)
			throws CacheControllerException {

		int blockWriteAddr = fsm.firstFreeBlock();

		if (blockWriteAddr == -1) {
			int blockToDie = this.lowestEntry.getKey();
			System.out.println("Adios bloque " + blockToDie);
			int cacheAddress = this.diskVSCache.get(blockToDie);
			this.diskVSCache.remove(blockToDie);
			this.diskVSFrequency.remove(blockToDie);
			rawWriteBlock(cacheAddress, 0, block);
			diskVSCache.put(hddAddr, cacheAddress);
			diskVSFrequency.put(hddAddr, frec);
			this.updateLowestEntry();
		} else {
			rawWriteBlock(blockWriteAddr, 0, block);
			diskVSCache.put(hddAddr, blockWriteAddr);
			diskVSFrequency.put(hddAddr, frec);
			this.updateLowestEntry();
		}
	}

	public byte[] readCacheBlock(int hddAddr) throws CacheControllerException {
		int cacheAddress = this.diskVSCache.get(hddAddr);
		byte[] blockPayload = rawReadBlock(cacheAddress);
		
		int newFreq = this.diskVSFrequency.get(hddAddr) + 1;
		this.diskVSFrequency.put(hddAddr, newFreq);
		updateLowestEntry();
		this.cacheHits++;

		return blockPayload;
	}

	public int getLowestFrec() {
		if (fsm.getNumberFreeBlocks() > 0) {
			return 0;
		} else {
			return lowestEntry.getValue();
		}
	}

	public int getLowestFrec2() {

		return lowestEntry.getValue();
	}

	public boolean isBlockInCache(int hddBlockAddr) {
		return this.diskVSCache.containsKey(hddBlockAddr);
	}

	private byte[] rawRead(int address, int offset, int length)
			throws CacheControllerException {

		byte[] readBuffer = new byte[length];
		int position = addressTranslation(address, offset);

		try {
			rawDeviceRW.seek(position);
			rawDeviceRW.read(readBuffer, 0, readBuffer.length);
			return readBuffer;
		} catch (IOException e) {

			throw new CacheControllerException("");
		}

	}

	private void rawWrite(int address, int offset, byte[] dataToWrite)
			throws CacheControllerException {

		try {
			int position = addressTranslation(address, offset);
			rawDeviceRW.seek(position);
			rawDeviceRW.write(dataToWrite, 0, dataToWrite.length);
		} catch (IOException e) {

			throw new CacheControllerException("");
		}

	}
	
public void rawWriteBlock(int address, int offset, byte[] dataToWrite) throws CacheControllerException{
		
		try {
			
			if(dataToWrite.length > CONFIG.BLOCK_SIZE){
				throw new CacheControllerException("An error occured trying to write a block on Cache, "
						+ "data[] size did not correspond to "+CONFIG.BLOCK_SIZE);
			}
			
			int position = addressTranslation(address, offset);
			rawDeviceRW.seek(position);
			rawDeviceRW.write(dataToWrite, 0, dataToWrite.length);
		} catch (IOException e) {
		
			throw new CacheControllerException("Could not write block on Cache.");
		}
		
	}
	
	public byte[] rawReadBlock(int address) throws CacheControllerException{
		
		byte[] readBuffer = new byte[CONFIG.BLOCK_SIZE-CONFIG.CONTROL_BYTES_SIZE];
		int position = addressTranslation(address, CONFIG.CONTROL_BYTES_SIZE);
		
		try {
			rawDeviceRW.seek(position);
			rawDeviceRW.read(readBuffer, 0 , readBuffer.length);
			return readBuffer;
		} catch (IOException e) {
			
			throw new CacheControllerException("Could not read Block on Cache.");
		}
		
		
	}

	private int addressTranslation(int address, int offset) {

		int position = METADATA_LENGTH + (CONFIG.BLOCK_SIZE * address) + offset;

		return position;
	}

	private void updateLowestEntry() {
		for (Map.Entry<Integer, Integer> entry : this.diskVSFrequency
				.entrySet()) {
			if (entry.getValue() < this.lowestEntry.getValue()) {
				this.lowestEntry = entry;
				// Si algo no funciona... quitar este break antes que nada
				break;
			}
		}

	}
	
	
	public long getCacheHits() {
		return this.cacheHits;
	}
	
	public ArrayList<Integer> listCacheContents() {
		ArrayList<Integer> ls = new ArrayList<Integer>();
		
		for (Integer key : this.diskVSCache.keySet()) {
			ls.add(key);
		}
		
		return ls;
	}
}
