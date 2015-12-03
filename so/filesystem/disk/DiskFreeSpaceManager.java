package so.filesystem.disk;

import java.util.ArrayList;

import so.filesystem.general.FreeSpaceManager;

public class DiskFreeSpaceManager extends FreeSpaceManager {

	private static DiskFreeSpaceManager self = null;

	private DiskFreeSpaceManager(ArrayList<byte[]> bitmapBytes) {
		super(bitmapBytes);
		
	}

	private DiskFreeSpaceManager(int deviceBlockSize) {
		super(deviceBlockSize);
		
	}


	public static DiskFreeSpaceManager getInstance(ArrayList<byte[]> bitmapBytes) {
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
	
	public static DiskFreeSpaceManager getInstance(){
		
		//TODO: SI ES NULL THORW EXCEPTION
		return self;
	}
}
