import java.util.BitSet;

public class FreeSpaceManager {
	BitSet diskSpaceBitMap;
	private int bitMapSizeInBlocks;
	/*
	 * By definition, 1 means free, 0 means used
	 */
	public FreeSpaceManager(byte[] bitmapBytes) {
		this.diskSpaceBitMap = new BitSet((bitmapBytes.length * 8) + 1);
		this.diskSpaceBitMap.set((bitmapBytes.length * 8) + 1, true);
		this.diskSpaceBitMap = BitSet.valueOf(bitmapBytes);
	}
	
	public FreeSpaceManager(int deviceBlockSize) {
		this.bitMapSizeInBlocks = ((deviceBlockSize/8)/4096)+1;
		this.diskSpaceBitMap = new BitSet((deviceBlockSize-this.bitMapSizeInBlocks)+1);
		this.diskSpaceBitMap.set(0, (deviceBlockSize-this.bitMapSizeInBlocks)+1, true);
		System.out.print("lengthBit: " + this.diskSpaceBitMap.length());
		//RandomAccessFile randomAccessFile = new RandomAccessFile("bitmap.txt", "rw");
	}
	
	public void freeBlocks(int block) {
		this.diskSpaceBitMap.set(block);
	}
	
	public void freeBlocks(int[] block) {
		for(int i = 0; i < block.length; ++i) {
			this.diskSpaceBitMap.set(block[i]);
		}
	}
	
	//DEBUG
	public void printbits() {
		byte[] logn =  this.diskSpaceBitMap.toByteArray();
		for (int i = 0 ; i< this.diskSpaceBitMap.toByteArray().length; ++i) {
			System.out.print(logn[i] + "\n");
		}
	}
	
	public byte[] updateFreeSpace() {
		return  this.diskSpaceBitMap.toByteArray();
	}
	
	public int firstFreeBlock() {
		if(this.getNumberFreeBlocks()<=0) {
			return -1;
		} else {
			int nextFreeBlock = this.diskSpaceBitMap.nextSetBit(0);
			this.diskSpaceBitMap.clear(nextFreeBlock);
			return nextFreeBlock;
		}
	}
	
	public int getBitMapSizeInBlocks() {
		return this.bitMapSizeInBlocks;
	}
	
	public int getNumberFreeBlocks() {
		return this.diskSpaceBitMap.cardinality() - 1;
	}
	
	public void closeBitMap() {
		
	}
}
