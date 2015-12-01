package so.filesystem.pruebas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.CRC32;

import so.filesystem.disk.DiskController;
import so.filesystem.disk.DiskControllerException;
import so.filesystem.disk.IncorrectLengthConversionException;

public class DeduplicationPlugin {
	private HashMap<Long, ArrayList<Integer>> blockHash;

	public DeduplicationPlugin() {
		this.blockHash = new HashMap<Long, ArrayList<Integer>>();
	}

	//Called if its proven that the block hasnt been added yet
	//Must recieve the PAYLOAD!
	public void addBlockToBlockHash(byte[] block, int blockAddr) throws IncorrectLengthConversionException, DiskControllerException {
		CRC32 hashGenerator = new CRC32();
		hashGenerator.update(block);
		long keyAdd = hashGenerator.getValue();
		if (this.blockHash.containsKey(keyAdd)) {
			this.blockHash.get(keyAdd).add(blockAddr);
		} else {
			this.blockHash.put(keyAdd, new ArrayList<Integer>());
			this.blockHash.get(keyAdd).add(blockAddr);
		}
		//DiskController.getInstance().incrementDeduplicationCounter(blockAddr);
	}
	
	//-1 is returned if the block does not exist
	public int[] isBlockInHash(byte[] block) {
		CRC32 hashGenerator = new CRC32();
		hashGenerator.update(block);
		long checkKey = hashGenerator.getValue();
		if (this.blockHash.containsKey(checkKey)) {
			int[] addresses = new int[this.blockHash.get(checkKey).size()];
			for (int i = 0; i < this.blockHash.get(checkKey).size(); ++i) {
				addresses[i] = this.blockHash.get(checkKey).get(i);
			}
			return addresses;
		} else {
			int[] addresses = new int[1];
			addresses[0] = -1;
			return addresses;
		}
	}
	
	public void removeFromHash(int blockAddr) throws DiskControllerException {
		CRC32 hashGenerator = new CRC32();
		byte[] block = DiskController.getInstance().rawReadBlockPayload(blockAddr);
		hashGenerator.update(block);
		long keyRemove = hashGenerator.getValue();	

		if (this.blockHash.get(keyRemove).size() == 1) {
			this.blockHash.remove(keyRemove);
		} else if (this.blockHash.get(keyRemove).size() > 1) {
			this.blockHash.get(keyRemove).remove(new Integer(blockAddr));
		}
	}
	
	//Debug
	public void removeFromHash(byte[] block, int blockAddr) {
		CRC32 hashGenerator = new CRC32();
		hashGenerator.update(block);
		long keyRemove = hashGenerator.getValue();	
		
		if (this.blockHash.get(keyRemove).size() == 1) {
			this.blockHash.remove(keyRemove);
		} else if (this.blockHash.get(keyRemove).size() > 1) {
			this.blockHash.get(keyRemove).remove(new Integer(blockAddr));
		}
	}
}