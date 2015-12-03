package so.filesystem.testing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import so.filesystem.disk.DiskControllerException;
import so.filesystem.disk.DiskFormatException;
import so.filesystem.disk.IncorrectLengthConversionException;
import so.filesystem.disk.UnidentifiedMetadataTypeException;
import so.filesystem.filemanagment.Inode;
import so.filesystem.filemanagment.InodeDirectPointerIndexOutOfRange;
import so.filesystem.filemanagment.InodeNotEnoughDiskSpaceExcepcion;
import so.filesystem.filemanagment.InodeReader;
import so.filesystem.general.CONFIG;
import so.filesystem.general.FreeSpaceManager;
import so.filesystem.disk.DeduplicationPlugin;
import so.filesystem.disk.DiskController;

public class IkerTestsMain {

	public static void main(String[] args)
			throws IncorrectLengthConversionException, DiskControllerException, IOException,
			InodeNotEnoughDiskSpaceExcepcion, InodeDirectPointerIndexOutOfRange, UnidentifiedMetadataTypeException {

		// TODO Auto-generated method stub
		// long diskSizeInBytes = new File("/Volumes/SO").getFreeSpace();
		// System.out.println("size " + new File("/Volumes/SO").getFreeSpace());

		// DeduplicationPlugin dedup = new DeduplicationPlugin();
		// dedup.addBlockToBlockHash("n0kJlcHK9iS0WzosIKhvWqeKNbYJcRZ9GbyUJzsOEZLXviVl2Z7rd753pKIeegWk4BVZuZuvOganjZ4NMcZ4sjCsgsCOgeG0rHoqnxblXqq1YKAkEdxmOT9dILNRu5Z2dVS1T1stpPsdk3D2CSdTOS2jN1C2ILOmzIYsygPgPMmFNUVAXbUDxHzTX5jfNpjmXGxnBCpRyNSbsfAznKTmpvCQmTpxEEHenaOpj2NQDSuMS0ZcYM0UvPT1mLh4U484RncdAEq29qiqKu2vQqNNHaMZOu44dXqplTclOKfI5HfOWhOJZkopxDtWzKxdD0ypmh5qIsG0juXzlleBFIb0bs9NpvWszj9hVoqtHmg7LWRxXcZZx6ThgAWXpwU9Z1bBjhL06ARMeaaKXYyskjNeMq5Y5X2cIPHCaSyrwBjdW3Knj0Kcytrgad5TX9WEJhu2H6tSiqAjls59OgTs0A9kuaJzNJCtF1mBY1miEK0Whvfc2wHcHcbvzcD5fKIbl7q11pGeSGgsot36CSzZen8vPw3FyoCZMPC5ePRerjZalEVGjR8IBA5LaendUPDDMYIUoSLfTzJuBKi8EwfLXhvtJe7AcYuGuyANhWPkc4lKt9NC".getBytes(),
		// 102);
		// dedup.addBlockToBlockHash("n0kJlcHK9iS0WzosIKhvWqeKNbYJcRZ9GbyUJzsOEZLXviVl2Z7rd753pKIeegWk4BVZuZuvOganjZ4NMcZ4sjCsgsCOgeG0rHoqnxblXqq1YKAkEdxmOT9dILNRu5Z2dVS1T1stpPsdk3D2CSdTOS2jN1C2ILOmzIYsygPgPMmFNUVAXbUDxHzTX5jfNpjmXGxnBCpRyNSbsfAznKTmpvCQmTpxEEHenaOpj2NQDSuMS0ZcYM0UvPT1mLh4U484RncdAEq29qiqKu2vQqNNHaMZOu44dXqplTclOKfI5HfOWhOJZkopxDtWzKxdD0ypmh5qIsG0juXzlleBFIb0bs9NpvWszj9hVoqtHmg7LWRxXcZZx6ThgAWXpwU9Z1bBjhL06ARMeaaKXYyskjNeMq5Y5X2cIPHCaSyrwBjdW3Knj0Kcytrgad5TX9WEJhu2H6tSiqAjls59OgTs0A9kuaJzNJCtF1mBY1miEK0Whvfc2wHcHcbvzcD5fKIbl7q11pGeSGgsot36CSzZen8vPw3FyoCZMPC5ePRerjZalEVGjR8IBA5LaendUPDDMYIUoSLfTzJuBKi8EwfLXhvtJe7AcYuGuyANhWPkc4lKt9NC".getBytes(),
		// 103);
		// dedup.addBlockToBlockHash("n0kJlcHK9iS0WzosIKhvWqeKNbYJcRZ9GbyUJzsOEZLXviVl2Z7rd753pKIeegWk4BVZuZuvOganjZ4NMcZ4sjCsgsCOgeG0rHoqnxblXqq1YKAkEdxmOT9dILNRu5Z2dVS1T1stpPsdk3D2CSdTOS2jN1C2ILOmzIYsygPgPMmFNUVAXbUDxHzTX5jfNpjmXGxnBCpRyNSbsfAznKTmpvCQmTpxEEHenaOpj2NQDSuMS0ZcYM0UvPT1mLh4U484RncdAEq29qiqKu2vQqNNHaMZOu44dXqplTclOKfI5HfOWhOJZkopxDtWzKxdD0ypmh5qIsG0juXzlleBFIb0bs9NpvWszj9hVoqtHmg7LWRxXcZZx6ThgAWXpwU9Z1bBjhL06ARMeaaKXYyskjNeMq5Y5X2cIPHCaSyrwBjdW3Knj0Kcytrgad5TX9WEJhu2H6tSiqAjls59OgTs0A9kuaJzNJCtF1mBY1miEK0Whvfc2wHcHcbvzcD5fKIbl7q11pGeSGgsot36CSzZen8vPw3FyoCZMPC5ePRerjZalEVGjR8IBA5LaendUPDDMYIUoSLfTzJuBKi8EwfLXhvtJe7AcYuGuyANhWPkc4lKt9NC".getBytes(),
		// 104);
		// dedup.addBlockToBlockHash("n0kJlcHK9iS0WzosIKhvWqeKNbYJcRZ9GbyUJzsOEZLXviVl2Z7rd753pKIeegWk4BVZuZuvOganjZ4NMcZ4sjCsgsCOgeG0rHoqnxblXqq1YKAkEdxmOT9dILNRu5Z2dVS1T1stpPsdk3D2CSdTOS2jN1C2ILOmzIYsygPgPMmFNUVAXbUDxHzTX5jfNpjmXGxnBCpRyNSbsfAznKTmpvCQmTpxEEHenaOpj2NQDSuMS0ZcYM0UvPT1mLh4U484RncdAEq29qiqKu2vQqNNHaMZOu44dXqplTclOKfI5HfOWhOJZkopxDtWzKxdD0ypmh5qIsG0juXzlleBFIb0bs9NpvWszj9hVoqtHmg7LWRxXcZZx6ThgAWXpwU9Z1bBjhL06ARMeaaKXYyskjNeMq5Y5X2cIPHCaSyrwBjdW3Knj0Kcytrgad5TX9WEJhu2H6tSiqAjls59OgTs0A9kuaJzNJCtF1mBY1miEK0Whvfc2wHcHcbvzcD5fKIbl7q11pGeSGgsot36CSzZen8vPw3FyoCZMPC5ePRerjZalEVGjR8IBA5LaendUPDDMYIUoSLfTzJuBKi8EwfLXhvtJe7AcYuGuyANhWPkc4lKt9NC".getBytes(),
		// 105);
		// dedup.addBlockToBlockHash("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".getBytes(),
		// 349);
		//
		// //System.out.println(dedup.isBlockInHash("a0kJlcHK9iS0WzosIKhvWqeKNbYJcRZ9GbyUJzsOEZLXviVl2Z7rd753pKIeegWk4BVZuZuvOganjZ4NMcZ4sjCsgsCOgeG0rHoqnxblXqq1YKAkEdxmOT9dILNRu5Z2dVS1T1stpPsdk3D2CSdTOS2jN1C2ILOmzIYsygPgPMmFNUVAXbUDxHzTX5jfNpjmXGxnBCpRyNSbsfAznKTmpvCQmTpxEEHenaOpj2NQDSuMS0ZcYM0UvPT1mLh4U484RncdAEq29qiqKu2vQqNNHaMZOu44dXqplTclOKfI5HfOWhOJZkopxDtWzKxdD0ypmh5qIsG0juXzlleBFIb0bs9NpvWszj9hVoqtHmg7LWRxXcZZx6ThgAWXpwU9Z1bBjhL06ARMeaaKXYyskjNeMq5Y5X2cIPHCaSyrwBjdW3Knj0Kcytrgad5TX9WEJhu2H6tSiqAjls59OgTs0A9kuaJzNJCtF1mBY1miEK0Whvfc2wHcHcbvzcD5fKIbl7q11pGeSGgsot36CSzZen8vPw3FyoCZMPC5ePRerjZalEVGjR8IBA5LaendUPDDMYIUoSLfTzJuBKi8EwfLXhvtJe7AcYuGuyANhWPkc4lKt9NC".getBytes())[0]);
		//// int[] bloq =
		// dedup.isBlockInHash("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".getBytes());
		////
		//// for (int i = 0; i < bloq.length; ++i) {
		//// System.out.println(bloq[i]);
		//// }
		//
		//// dedup.removeFromHash("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".getBytes(),
		// 349);
		// int [] bloq =
		// dedup.isBlockInHash("n0kJlcHK9iS0WzosIKhvWqeKNbYJcRZ9GbyUJzsOEZLXviVl2Z7rd753pKIeegWk4BVZuZuvOganjZ4NMcZ4sjCsgsCOgeG0rHoqnxblXqq1YKAkEdxmOT9dILNRu5Z2dVS1T1stpPsdk3D2CSdTOS2jN1C2ILOmzIYsygPgPMmFNUVAXbUDxHzTX5jfNpjmXGxnBCpRyNSbsfAznKTmpvCQmTpxEEHenaOpj2NQDSuMS0ZcYM0UvPT1mLh4U484RncdAEq29qiqKu2vQqNNHaMZOu44dXqplTclOKfI5HfOWhOJZkopxDtWzKxdD0ypmh5qIsG0juXzlleBFIb0bs9NpvWszj9hVoqtHmg7LWRxXcZZx6ThgAWXpwU9Z1bBjhL06ARMeaaKXYyskjNeMq5Y5X2cIPHCaSyrwBjdW3Knj0Kcytrgad5TX9WEJhu2H6tSiqAjls59OgTs0A9kuaJzNJCtF1mBY1miEK0Whvfc2wHcHcbvzcD5fKIbl7q11pGeSGgsot36CSzZen8vPw3FyoCZMPC5ePRerjZalEVGjR8IBA5LaendUPDDMYIUoSLfTzJuBKi8EwfLXhvtJe7AcYuGuyANhWPkc4lKt9NC".getBytes());
		//// System.o
		//
		// //System.out.println("Removed");
		// for (int i = 0; i < bloq.length; ++i) {
		// System.out.println(bloq[i]);
		// }
		// dedup.closeDeduplicationPlugin();
		ArrayList<byte[]> hola = new ArrayList<byte[]>();
		for (int j = 0; j < 169; ++j) {
			hola.add("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".getBytes());
			//System.out.println(hola.get(j).length);
		}
		//System.out.println(hola.size());
		
		long byteSize = (long) 22655952486.0;
		long numberOfBlocks = ((byteSize - new Long(CONFIG.INITIAL_METADATA_SIZE))/ new Long(CONFIG.BLOCK_SIZE));

		System.out.println("Number of Blocks: " + numberOfBlocks);
		FreeSpaceManager fsm = new FreeSpaceManager(hola);
		System.out.println("Free Blocks: " + fsm.getNumberFreeBlocks());
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
//		fsm.firstFreeBlock();
		System.out.println("Size arraList: " + fsm.updateFreeSpace().size());
		System.out.println("BitMapSizeBlocks: " + fsm.getBitMapSizeInBlocks());
		System.out.println("FirstFreeBlock: " + fsm.firstFreeBlock());
		System.out.println("Total Blocks: " + ((fsm.getBitMapSizeInBlocks())-fsm.getBitMapSizeInBlocks()));
		System.out.println("Free Blocks: " + fsm.getNumberFreeBlocks());
		System.out.println("Free Disk %: " + ( (float) fsm.getNumberFreeBlocks()/((float)(fsm.getBitMapSizeInBlocks()* 8 * 4096)-fsm.getBitMapSizeInBlocks()))* 100);
		System.out.println("Total Storage Space: " + (long)fsm.getBitMapSizeInBlocks() * 8 * CONFIG.BLOCK_SIZE * CONFIG.BLOCK_SIZE);
		System.out.println("Used Storage Space: " + (((long)fsm.getBitMapSizeInBlocks() * 8 * CONFIG.BLOCK_SIZE * CONFIG.BLOCK_SIZE) - ((long) fsm.getNumberFreeBlocks() * 4096)));
		
	}

}
