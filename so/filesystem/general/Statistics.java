package so.filesystem.general;

import java.util.ArrayList;

import so.filesystem.disk.DiskFreeSpaceManager;

public class Statistics {
	public ArrayList<String> getAllDiskStatistics() {
		ArrayList<String> statisticsBundle =  new ArrayList<String>();
		
		String freeBlocks = "Number of free blocks: " + DiskFreeSpaceManager.getInstance().getNumberFreeBlocks() + " blocks.";
		statisticsBundle.add(freeBlocks);
		
		String usedBlocks = "Number of used blocks: " + (DiskFreeSpaceManager.getInstance().getBitmapSize() - DiskFreeSpaceManager.getInstance().getNumberFreeBlocks()) + " blocks.";
		statisticsBundle.add(usedBlocks);
		
		String totalBlocks = "Total number of blocks in disk: " + DiskFreeSpaceManager.getInstance().getBitmapSize() + " blocks.";
		statisticsBundle.add(totalBlocks);
	
		String sizeOfBitmap = "Bitmap size in Blocks: " + DiskFreeSpaceManager.getInstance().getBitMapSizeInBlocks() + " blocks.";
		statisticsBundle.add(sizeOfBitmap);
		
		String diskUsage = "% of free space: " + (DiskFreeSpaceManager.getInstance().getNumberFreeBlocks()/DiskFreeSpaceManager.getInstance().getBitmapSize()) * 100 + "%.";
		statisticsBundle.add(diskUsage);
		
		return statisticsBundle;
	}
}
