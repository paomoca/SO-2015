package so.filesystem.general;
public class CONFIG {

	public static int BLOCK_SIZE = 40;
	public static int ADDRESS_SIZE = 4;
	public static int DIRECT_POINTERS = 2;
	public static int IDB_TOTAL_ADDRESSES = BLOCK_SIZE/ADDRESS_SIZE;
	public static int CONTROL_BYTES_SIZE = 4;
	public static String DISK_LOCATION = "/dev/s...";
	
}
