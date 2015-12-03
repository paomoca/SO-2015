package so.filesystem.cache;


import so.filesystem.general.FreeSpaceManager;

public class CacheFreeSpaceManager extends FreeSpaceManager {
	
	private static CacheFreeSpaceManager self = null;

//	private CacheFreeSpaceManager(byte[] bitmapBytes) {
//		super(bitmapBytes);
//		
//	}

	private CacheFreeSpaceManager(int deviceBlockSize) {
		super(deviceBlockSize);
		
	}

//	public static CacheFreeSpaceManager getInstance(byte[] bitmapBytes) {
//		if (self == null) {
//			self = new CacheFreeSpaceManager(bitmapBytes);
//		}
//		return self;
//	}

	public static CacheFreeSpaceManager getInstance(int deviceBlockSize) {
		if (self == null) {
			self = new CacheFreeSpaceManager(deviceBlockSize);
		}
		return self;
	}

}
