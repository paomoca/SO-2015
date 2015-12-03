package so.filesystem.general;

import so.gui.grid.BlockGrid;

import java.util.ArrayList;
import java.util.BitSet;

import so.gui.grid.SectionGrid;

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

	public FreeSpaceManager(ArrayList<?> bitmap) {
		ArrayList<byte[]> bitmapBytes = (ArrayList<byte[]>) bitmap;
		this.diskSpaceBitMap = new BitSet(bitmapBytes.size() * CONFIG.BLOCK_SIZE * 8);
		this.bitMapSizeInBlocks = bitmapBytes.size();
		byte[] superArray = new byte[bitmapBytes.size() * CONFIG.BLOCK_SIZE];
		int multiplier = 0;
		for (byte[] block : bitmapBytes) {
			int j = CONFIG.BLOCK_SIZE * multiplier;
			for (int i = 0; i < CONFIG.BLOCK_SIZE; ++i) {
				superArray[i + j] = block[i];
			}
			multiplier++;
		}
		this.diskSpaceBitMap = BitSet.valueOf(superArray);
		this.diskSpaceBitMap.set(CONFIG.METADATA_DIRECTORY_ADDRESS_REFERENCE, false);
	}

	public FreeSpaceManager(int deviceBlockSize) {
//		this.bitMapSizeInBlocks = ((deviceBlockSize / 8) / CONFIG.BLOCK_SIZE)+1;
		this.bitMapSizeInBlocks = (int) Math.ceil((((double) deviceBlockSize)/ 8) /CONFIG.BLOCK_SIZE);
		this.diskSpaceBitMap = new BitSet((deviceBlockSize - this.bitMapSizeInBlocks) + 1);
		this.diskSpaceBitMap.set(0, (deviceBlockSize - this.bitMapSizeInBlocks) + 1, true);
		this.diskSpaceBitMap.set(CONFIG.METADATA_DIRECTORY_ADDRESS_REFERENCE, false);

		// RandomAccessFile randomAccessFile = new
		// RandomAccessFile("bitmap.txt", "rw");
	}

	public void freeBlocks(int block) {
		System.out.println("delete block: "+block);
		this.diskSpaceBitMap.set(block);
	}

	public void freeBlocks(int[] block) {
		for (int i = 0; i < block.length; ++i) {
			this.diskSpaceBitMap.set(block[i]);
		}
	}

	// DEBUG
	public void printbits() {
		byte[] logn = this.diskSpaceBitMap.toByteArray();
		for (int i = 0; i < this.diskSpaceBitMap.toByteArray().length; ++i) {
			System.out.print(logn[i] + "\n");
		}
	}

	public boolean[] printbits(int begin, int end) {
		ArrayList<Boolean> arrayBits = new ArrayList<Boolean>();
		boolean[] bits;

		for (int i = begin; i <= end; ++i) {
			arrayBits.add(this.diskSpaceBitMap.get(i));
		}
		bits = new boolean[arrayBits.size()];
		for (int i = 0; i < arrayBits.size(); ++i) {
			bits[i] = !arrayBits.get(i);
		}

		BlockGrid.getInstance().fillCells(bits);
		return bits;
	}

	public ArrayList<byte[]> updateFreeSpace() {
		byte[] bytesBit = this.diskSpaceBitMap.toByteArray();

//		byte[] bytesBit = new byte[(this.diskSpaceBitMap.length() + 7 )/ 8 ];
//		for (int i = 0; i < this.diskSpaceBitMap.length(); i++) {
//			if (this.diskSpaceBitMap.get(i)) {
//				bytesBit[bytesBit.length - i / 8 - 1] |= 1 << (i % 8);
//			}
//		}

		// byte[][] bitmapInBlocks = new byte[bytesBit.length][CONFIG.BLOCK_SIZE];
		ArrayList<byte[]> bytesToWrite = new ArrayList<byte[]>();
		System.out.println("bytesBit length: " + bytesBit.length);
		for (int i = 0; i < this.bitMapSizeInBlocks; i++) {
			byte[] temp = new byte[CONFIG.BLOCK_SIZE];
			for (int j = 0; j < CONFIG.BLOCK_SIZE; j++) {
				try {
					temp[j] = bytesBit[j + i];
				} catch (ArrayIndexOutOfBoundsException e) {
					temp[j] = 0;
				}
			}
			bytesToWrite.add(temp);
		}

		return bytesToWrite;
	}

	public int firstFreeBlock() {
		if (this.getNumberFreeBlocks() <= 0) {
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
	
	public int getBitmapSize() {
		return this.diskSpaceBitMap.length();
	}
	public void closeBitMap() {

	}
}
