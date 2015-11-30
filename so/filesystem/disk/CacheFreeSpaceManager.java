package so.filesystem.disk;

public class CacheFreeSpaceManager extends FreeSpaceManager {
	
	private static CacheFreeSpaceManager self = null;

	public CacheFreeSpaceManager(byte[] bitmapBytes) {
		super(bitmapBytes);
		// TODO Auto-generated constructor stub
	}

	public CacheFreeSpaceManager(int deviceBlockSize) {
		super(deviceBlockSize);
		// TODO Auto-generated constructor stub
	}

	public static CacheFreeSpaceManager getInstance(byte[] bitmapBytes) {
		if (self == null) {
			self = new CacheFreeSpaceManager(bitmapBytes);
		}
		return self;
	}

	public static CacheFreeSpaceManager getInstance(int deviceBlockSize) {
		if (self == null) {
			self = new CacheFreeSpaceManager(deviceBlockSize);
		}
		return self;
	}

}
