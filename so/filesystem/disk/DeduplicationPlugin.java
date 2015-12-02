package so.filesystem.disk;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.CRC32;

import so.filesystem.disk.DiskController;
import so.filesystem.disk.DiskControllerException;
import so.filesystem.disk.IncorrectLengthConversionException;
import so.filesystem.general.CONFIG;

public class DeduplicationPlugin {
	private HashMap<Long, ArrayList<Integer>> blockHash;

	// Check where the table will be stored
	public DeduplicationPlugin() {

		try {
			FileInputStream fos = new FileInputStream("t.tmp");
			ObjectInputStream oos = new ObjectInputStream(fos);
			try {
				this.blockHash = (HashMap<Long, ArrayList<Integer>>) oos.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			this.blockHash = new HashMap<Long, ArrayList<Integer>>();
			// e.printStackTrace();
		}

	}

	// Called if its proven that the block hasnt been added yet
	// Must recieve the PAYLOAD!
	public void addBlockToBlockHash(byte[] block, int blockAddr)
			throws IncorrectLengthConversionException, DiskControllerException {
		CRC32 hashGenerator = new CRC32();
		hashGenerator.update(block);
		long keyAdd = hashGenerator.getValue();
		if (this.blockHash.containsKey(keyAdd)) {
			if (!this.blockHash.get(keyAdd).contains(blockAddr)) {
				this.blockHash.get(keyAdd).add(blockAddr);
			} else {
				System.out.println("This block already exists!!");
			}
		} else {
			this.blockHash.put(keyAdd, new ArrayList<Integer>());
			this.blockHash.get(keyAdd).add(blockAddr);
		}
		// DiskController.getInstance().incrementDeduplicationCounter(blockAddr);
	}

	// -1 is returned if the block does not exist
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

		if (this.blockHash.containsKey(keyRemove)) {
			if (this.blockHash.get(keyRemove).size() == 1) {
				this.blockHash.remove(keyRemove);
			} else if (this.blockHash.get(keyRemove).size() > 1) {
				this.blockHash.get(keyRemove).remove(new Integer(blockAddr));
			}
		}
	}

	// Debug
	public void removeFromHash(byte[] block, int blockAddr) {
		CRC32 hashGenerator = new CRC32();
		hashGenerator.update(block);
		long keyRemove = hashGenerator.getValue();

		if (this.blockHash.containsKey(keyRemove)) {
			if (this.blockHash.containsKey(keyRemove)) {
				if (this.blockHash.get(keyRemove).size() == 1) {
					this.blockHash.remove(keyRemove);
				} else if (this.blockHash.get(keyRemove).size() > 1) {
					this.blockHash.get(keyRemove).remove(new Integer(blockAddr));
				}
			} else {
				if (CONFIG.DEBUG_SESSION) {
					System.out.println("Block Not in Hash");
				}
			}
		}
	}

	public void closeDeduplicationPlugin() {
		try {
			FileOutputStream fos = new FileOutputStream("t.tmp");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this.blockHash);
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}