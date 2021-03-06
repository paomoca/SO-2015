package so.filesystem.testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;

import so.filesystem.disk.DiskController;
import so.filesystem.disk.DiskControllerException;
import so.filesystem.disk.DiskFormatException;
import so.filesystem.disk.DiskFreeSpaceManager;
import so.filesystem.disk.IncorrectLengthConversionException;
import so.filesystem.disk.UnidentifiedMetadataTypeException;
import so.filesystem.filemanagment.InodeDirectPointerIndexOutOfRange;
import so.filesystem.filemanagment.InodeFileTooBigException;
import so.filesystem.filemanagment.InodeNotEnoughDiskSpaceExcepcion;
import so.filesystem.filemanagment.InodeReader;
import so.filesystem.filemanagment.InodeWriter;
import so.filesystem.general.CONFIG;

public class PaoTestsMain {


	public static void main(String[] args) throws UnidentifiedMetadataTypeException, IOException, InodeNotEnoughDiskSpaceExcepcion, InodeDirectPointerIndexOutOfRange, InodeFileTooBigException, IncorrectLengthConversionException {

		try {

			
			String string = "aei aei aei";
			//System.out.println(new File("/Volumes/SO").getTotalSpace());
			boolean skipped_debug = false;
			if(CONFIG.DEBUG_SESSION){
				CONFIG.DEBUG_SESSION = false;
				skipped_debug = true;
			}
			DiskController dc = DiskController.getInstance(true);
			System.out.println("Free blocks: "+DiskFreeSpaceManager.getInstance().getNumberFreeBlocks());
			System.out.println("Metadata antes: "+dc.METADATA_LENGTH);
			
			InodeWriter inodeW = new InodeWriter();
			
			for(int i = 0; i< 10031; i++){
				DiskFreeSpaceManager.getInstance().firstFreeBlock();
				inodeW.inodeWriteWalker(i);
				
			}
			
			dc.finalize();
			DiskController dc2 = DiskController.getInstance(false);
			InodeReader inodeRr = new InodeReader(1);
			
			System.out.println("New read");
			for(int i = 0; i< 10031; i++){
				
				//System.out.println(inodeRr.inodeReadWalkerNext());
			}
			System.out.println("Metadata despues: "+dc.METADATA_LENGTH);
			System.out.println("Free blocks: "+DiskFreeSpaceManager.getInstance().getNumberFreeBlocks());
			dc2.finalize();
			
			if(skipped_debug){
				CONFIG.DEBUG_SESSION = true;
			}
			

			System.out.println("Metadata length: "+dc.METADATA_LENGTH);
			System.out.println(CONFIG.DEBUG_SESSION);
			
			
			boolean write = false;
			boolean counter = false;
			
			/*if(write){
				dc.rawWriteBlockPayload(10, string.getBytes(), string.length());
				dc.rawWriteBlockPayload(37, string.getBytes(), string.length());
				dc.rawWriteBlockPayload(57, string.getBytes(), string.length());
				dc.rawWriteBlockPayload(77, string.getBytes(), string.length());
				dc.rawWriteBlockPayload(97, string.getBytes(), string.length());
				dc.rawWriteBlockPayload(117, string.getBytes(), string.length());
			} 
				
				
				System.out.println(new String(dc.rawReadBlockPayload(10)));
				System.out.println(new String(dc.rawReadBlockPayload(37)));
				System.out.println(new String(dc.rawReadBlockPayload(57)));
				System.out.println(new String(dc.rawReadBlockPayload(77)));
				System.out.println(new String(dc.rawReadBlockPayload(97)));
				
				System.out.println(new String(dc.rawReadBlock(117)));
			
			
			if(counter){
				
				dc.setDeduplicationCounter(10, 10);
				dc.incrementDeduplicationCounter(10);
				dc.setDeduplicationCounter(37, 20);
				dc.incrementDeduplicationCounter(37);
				dc.setDeduplicationCounter(57, 30);
				dc.incrementDeduplicationCounter(57);
				dc.setDeduplicationCounter(77, 40);
				dc.incrementDeduplicationCounter(77);
				
			} 
				
				System.out.println(dc.getDeduplicationCounter(10));
				System.out.println(dc.getDeduplicationCounter(37));
				System.out.println(dc.getDeduplicationCounter(57));
				System.out.println(dc.getDeduplicationCounter(77));
			
			
			
			if(counter){
				
				dc.setBlockAccessFrequency(10, 167);
				dc.incrementBlockAccessFrequency(10);
				dc.setBlockAccessFrequency(37, 2);
				dc.incrementBlockAccessFrequency(37);
				dc.setBlockAccessFrequency(57, 3);
				dc.incrementBlockAccessFrequency(57);
				dc.setBlockAccessFrequency(77, 4);
				dc.incrementBlockAccessFrequency(77);
				
			} 
				System.out.println("freq");
				System.out.println(dc.getBlockAccessFrequency(10));
				System.out.println(dc.getBlockAccessFrequency(37));
				System.out.println(dc.getBlockAccessFrequency(57));
				System.out.println(dc.getBlockAccessFrequency(77));
			
			
			*/
			
			/*System.out.println(new String(dc.rawReadBlock(20)));
			dc.rawWriteBlockPayload(21, "hola como estas".getBytes(), "hola como estas".length());
			System.out.println(new String(dc.rawReadBlockPayload(21)));
			dc.rawAddressWrite(1,20, 12);
			System.out.println(dc.rawAddressRead(1, 20));
			System.out.println(dc.bytesToInt(dc.intToBytes(4, 12)));*/
			
			//DiskFreeSpaceManager.getInstance(40000000);
			
			System.out.println("Free blocks: "+DiskFreeSpaceManager.getInstance().getNumberFreeBlocks());
			int tope = 4000;
			
//			InodeWriter inodeW = new InodeWriter();
//			
//			for(int i = 1000; i< tope; i++){
//				inodeW.inodeWriteWalker(i);
//				
//			}
			
//			InodeReader inodeR = new InodeReader(1);
//			
//			for(int i = 1000; i< tope; i++){
//				
//				inodeR.inodeReadWalkerNext();
//			}
////			
			//System.out.println("DFSM free: "+DiskFreeSpaceManager.getInstance().getNumberFreeBlocks());
				
			dc.finalize();
	
			
		} catch (DiskControllerException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		} catch (DiskFormatException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		} 

	}

}
