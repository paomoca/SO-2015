package so.filesystem.cache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import so.filesystem.disk.DeviceInitializationException;
import so.filesystem.disk.DiskControllerException;
import so.filesystem.general.CONFIG;
import so.filesystem.general.FreeSpaceManager;

import java.util.*;

public class CacheController {
	
	private static CacheController self = null;

	private int METADATA_LENGTH;
	private int cacheHits;
	private Entry<Integer, Integer> lowestEntry;
	byte[] cacheSystemKey = "acCiWJJwrNg6RRyCyOns8zOmYTjK06GFZaLRkttMYHUXGUBcXZ8VvqBbPGwch9UMyaPFmVGRMfZujgiJzAwLsBWIfzKmAHHsqJZ5L5jg4iUo7VvGED8JbTgGXMHKZqTiyp1Cj8c2A8E9KbG2tnRkoKk56oHl2w3a1XF0Dz82It6pEmVfKLzLbp1ogZu2LEuUVcONkNkI0fvYEWlzCoeXAwIvEvVwKG2yZq1fYCB8HPwNfMka9Gv10x29shCWMoOaOq63RrD5xAsINqONtlCnFOkmct1rE53Wos8UXx8ZuqYXps7gbMCNhPFvO8UGLB1XWDRcAcbqvtxoYLMTqAEvQ2crCP708GuMcTiINoUymJbecGfJhxyy9yha248vGW0BzUG14ccptuaILzlOt3PEsSnofmC9J9u8JMRls1RS1cPeAiPxG5tt8O2234wK5teSJCAKYs93qa2bzElA8MrPpokF7v2qN0b4UPpCl34l3TDCnpj0mHx8jILlKHCBwuSXnqeecjcte16HDbbRiB1WoTUcrwusCnubxYrUfK5FMZEpOgYhlFmysPZ4Ym2L4zUXTHlRBEeuNjBuKRExigyquvtDlVCi0eZczxfHvoKcKDr1wMynG4PS6KIaqf0byScxHMsgTTuALoWxsrlW8oiXRuy8KmL9BnCOY5CbNA4TFLw2ljzhjNEoCWrpl28YS1Z256yzq9SSGVDVvmTiAqDpYR8mgUZ71nvE8eiz9L39kUphyqSYLZR5lqYuDrlaOJz4x7ciInl2KqlJeRM9azrF5wb2fA2iD7Fm1Q2acRt9MO3XuRwgazI1DktaF0ghOUKYTwamGttUqJoxyX1c83znnA1j6c7mtZD18ZAMtNJl4y6VxGhk8ZN3XeSoSrUscke1OjN84p54ggU0iY50nWvOjFK3CB5QJbOfZDHxLqMi9mLk0vkDJ6c9rzk5w9FhE5ub4O5n5JsaLMgzMYDlPCSwQnpH1BKWkcsOIEcEZesiF1TikxieQNloZrjqX6RpYpZl"
			.getBytes();

	private RandomAccessFile rawDeviceRW;
	private CacheFreeSpaceManager fsm;
	private HashMap<Integer, Integer> diskVSCache;
	private HashMap<Integer, Integer> diskVSFrequency;

	private CacheController(boolean formatFlag)
			throws CacheControllerException, CacheFormatException {

		diskVSCache = new HashMap<Integer, Integer>();
		diskVSFrequency = new HashMap<Integer, Integer>();

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

			inKey = rawRead(0, 0, cacheSystemKey.length);
			if (!Arrays.equals(cacheSystemKey, inKey)) {
				throw new CacheFormatException(
						"This device is already initialized with a different FileSystem.");
			}

			// If everything went well we add the size of the key to
			// METADATA_LENGTH.
			METADATA_LENGTH += cacheSystemKey.length;

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

			rawWrite(0, 0, cacheSystemKey);

			// If everything went well we add the size of the key to
			// METADATA_LENGTH.
			METADATA_LENGTH += cacheSystemKey.length;

		} catch (CacheControllerException e) {
			throw new CacheFormatException(
					"An error occured trying formatting the device.");
		}
	}

	public void freeSpaceManagerInitialization()
			throws CacheControllerException {

		try {

			int numberOfBlocks = (int) ((rawDeviceRW.length() - cacheSystemKey.length) / CONFIG.BLOCK_SIZE);
			System.out.println("bblocks av: " + numberOfBlocks);
			fsm = CacheFreeSpaceManager.getInstance(numberOfBlocks);

		} catch (IOException e) {

			throw new CacheControllerException(
					"Unable to initialize Free Space Manager.");
		}
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
}
