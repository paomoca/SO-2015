package so.filesystem.disk;

public class DiskFreeSpaceManager extends FreeSpaceManager {

	private static DiskFreeSpaceManager self = null;

	public DiskFreeSpaceManager(byte[] bitmapBytes) {

		super(bitmapBytes);
	}

	public DiskFreeSpaceManager(int deviceBlockSize) {

		super(deviceBlockSize);
	}

	public static DiskFreeSpaceManager getInstance(byte[] bitmapBytes) {
		if (self == null) {
			self = new DiskFreeSpaceManager(bitmapBytes);
		}
		return self;
	}

	public static DiskFreeSpaceManager getInstance(int deviceBlockSize) {
		if (self == null) {
			self = new DiskFreeSpaceManager(deviceBlockSize);
		}
		return self;
	}
}
