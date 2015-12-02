package so.filesystem.testing;

import java.io.IOException;
import java.util.Arrays;

import so.filesystem.disk.DiskController;
import so.filesystem.disk.DiskControllerException;
import so.filesystem.disk.DiskFormatException;
import so.filesystem.disk.DiskFreeSpaceManager;
import so.filesystem.disk.IncorrectLengthConversionException;
import so.filesystem.disk.UnidentifiedMetadataTypeException;
<<<<<<< HEAD
=======
import so.filesystem.filemanagment.InodeDirectPointerIndexOutOfRange;
import so.filesystem.filemanagment.InodeFileTooBigException;
import so.filesystem.filemanagment.InodeNotEnoughDiskSpaceExcepcion;
import so.filesystem.filemanagment.InodeReader;
import so.filesystem.filemanagment.InodeWriter;
>>>>>>> 1ea966ce8337833cfd7564bbfb10ea46d9dacf31
import so.filesystem.general.CONFIG;

public class PaoTestsMain {

<<<<<<< HEAD
	public static void main(String[] args) throws UnidentifiedMetadataTypeException, IOException {
=======
	public static void main(String[] args) throws UnidentifiedMetadataTypeException, IOException, InodeNotEnoughDiskSpaceExcepcion, InodeDirectPointerIndexOutOfRange, InodeFileTooBigException {
>>>>>>> 1ea966ce8337833cfd7564bbfb10ea46d9dacf31
		
		try {
			
			String string = "aei aei aei";
			DiskController dc = DiskController.getInstance(true);
			System.out.println("Metadata length: "+dc.METADATA_LENGTH);
			
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
			
			DiskFreeSpaceManager.getInstance(40000);
			
			
			//InodeWriter inodeW = new InodeWriter();
			
			for(int i = 0; i< 20; i++){
				//inodeW.inodeWriteWalker(i);
				System.out.println(DiskFreeSpaceManager.getInstance().firstFreeBlock());
			}
			
			/*InodeReader inodeR = new InodeReader(1);
			
			for(int i = 0; i< 2200; i++){
				inodeR.inodeReadWalkerNext();
			}*/
			
				
			dc.finalize();
			

			
			
			
		} catch (DiskControllerException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		} catch (DiskFormatException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		} /*catch (IncorrectLengthConversionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/ 

	}

}
