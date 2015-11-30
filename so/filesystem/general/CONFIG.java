package so.filesystem.general;
public class CONFIG {

	public static boolean DEBUG_SESSION = true;
	
	public static int BLOCK_SIZE = 40;
	
	//There must be a correspondence between the offset and the amount of control bytes.
	public static int DEDUPLICATION_CONTROL_OFFSET = 0;
	public static int DEDUPLICATION_CONTROL_SIZE = 2;
	public static int FREQUENCY_CONTROL_OFFSET = DEDUPLICATION_CONTROL_SIZE; 
	public static int FREQUENCY_CONTROL_SIZE = 2;
	
	public static int CONTROL_BYTES_SIZE = FREQUENCY_CONTROL_SIZE+DEDUPLICATION_CONTROL_SIZE;
	
	public static int BLOCK_PAYLOAD_SIZE = BLOCK_SIZE-CONTROL_BYTES_SIZE;
	public static int ADDRESS_SIZE = 4;
	public static int DIRECT_POINTERS = 2;
	public static int IDB_TOTAL_ADDRESSES = BLOCK_SIZE/ADDRESS_SIZE;

	public static String DISK_LOCATION = "diskRawDeviceTestFile";
	public static String CACHE_LOCATION = "cacheRawDeviceTestFile";
	
	//Do not modify. No more than 2 bytes should be used.
	public static int FREE_SPACE_MANAGER_INITIAL_CONTROL_BYTE_SIZE=2;
	
}
