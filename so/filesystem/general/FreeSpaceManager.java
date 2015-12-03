package so.filesystem.general;

import java.util.ArrayList;
import java.util.BitSet;

public class FreeSpaceManager {
	private BitSet diskSpaceBitMap;
	private int bitMapSizeInBlocks;
	/*
	 * By definition, 1 means free, 0 means used
	 */	
	public FreeSpaceManager(byte[] bitmapBytes) {
		this.diskSpaceBitMap = new BitSet((bitmapBytes.length * 8) + 1);
		this.diskSpaceBitMap.set((bitmapBytes.length * 8) + 1, true);
		this.diskSpaceBitMap = BitSet.valueOf(bitmapBytes);
		this.diskSpaceBitMap.set(CONFIG.METADATA_DIRECTORY_ADDRESS_REFERENCE, false);
	}
	
	public FreeSpaceManager(ArrayList<byte[]> bitmapBytes) {
		this.diskSpaceBitMap = new BitSet(bitmapBytes.size() * 4096 * 8);
		this.bitMapSizeInBlocks = bitmapBytes.size();
		byte[] superArray = new byte[bitmapBytes.size() * 4096];
		int multiplier = 0;
		for(byte[] block : bitmapBytes) {
			int j = 4096 * multiplier;
			for (int i = 0; i < 4096; ++i) {
				superArray[i + j] = block[i];
			}
			multiplier++;
		}
		this.diskSpaceBitMap = BitSet.valueOf(superArray);
		this.diskSpaceBitMap.set(CONFIG.METADATA_DIRECTORY_ADDRESS_REFERENCE, false);
	}
	
	public FreeSpaceManager(int deviceBlockSize) {
		this.bitMapSizeInBlocks = ((deviceBlockSize/8)/4096)+1;
		this.diskSpaceBitMap = new BitSet((deviceBlockSize-this.bitMapSizeInBlocks)+1);
		this.diskSpaceBitMap.set(0, (deviceBlockSize-this.bitMapSizeInBlocks)+1, true);
		this.diskSpaceBitMap.set(CONFIG.METADATA_DIRECTORY_ADDRESS_REFERENCE, false);

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
	
	public boolean[] printbits(int begin, int end) {
		ArrayList<Boolean> arrayBits = new ArrayList<Boolean>();
		boolean[] bits;
		
		for(int i = begin; i <= end; ++i) {
			arrayBits.add(this.diskSpaceBitMap.get(i));
		}
		bits = new boolean[arrayBits.size()];
		for(int i = 0; i < arrayBits.size(); ++i) {
			bits[i] = !arrayBits.get(i);
		}
		
		return bits;
	}
	
	public ArrayList<byte[]> updateFreeSpace() {
		byte[] bytesBit = this.diskSpaceBitMap.toByteArray();
		//byte[][] bitmapInBlocks = new byte[bytesBit.length][4096];
		ArrayList<byte[]> bytesToWrite = new ArrayList<byte[]>();
		
		for (int i = 0; i <= bytesBit.length/4096; ++i) {
			int multiplier = i * 4096;
			byte[] temp = new byte[4096];
			for(int j = 0; j < 4096; ++j) {
				temp[j] = bytesBit[j + i];
			}
			bytesToWrite.add(temp);
		}
		
		return bytesToWrite;
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
