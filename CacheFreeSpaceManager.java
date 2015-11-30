

import so.filesystem.general.FreeSpaceManager;

public class CacheFreeSpaceManager extends FreeSpaceManager {
	
	private static CacheFreeSpaceManager self = null;

	public CacheFreeSpaceManager(byte[] bitmapBytes) {
		super(bitmapBytes);
		
	}

	public CacheFreeSpaceManager(int deviceBlockSize) {
		super(deviceBlockSize);
		
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
