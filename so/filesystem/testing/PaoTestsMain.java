package so.filesystem.testing;

import so.filesystem.disk.DiskController;
import so.filesystem.disk.DiskControllerException;
import so.filesystem.disk.DiskFormatException;
import so.filesystem.disk.IncorrectLengthConversionException;
import so.filesystem.general.CONFIG;

public class PaoTestsMain {

	public static void main(String[] args) {
		
		try {
			
			String string = "aei aei aei";
			DiskController dc = DiskController.getInstance(true);
			
			boolean write = false;
			boolean counter = true;
			
			if(write){
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
			
			
			
			
			/*System.out.println(new String(dc.rawReadBlock(20)));
			dc.rawWriteBlockPayload(21, "hola como estas".getBytes(), "hola como estas".length());
			System.out.println(new String(dc.rawReadBlockPayload(21)));
			dc.rawAddressWrite(1,20, 12);
			System.out.println(dc.rawAddressRead(1, 20));
			System.out.println(dc.bytesToInt(dc.intToBytes(4, 12)));*/
			
			dc.finalize();
			
			int TOTAL_ADDRESSED_BLOCKS = 30456;
			double INTERNAL_FRAGMENTATION = 0;
			
			int t = (int) Math.ceil((double)TOTAL_ADDRESSED_BLOCKS/CONFIG.BLOCK_SIZE);
			double r = (CONFIG.BLOCK_SIZE*t)-TOTAL_ADDRESSED_BLOCKS;
			
			System.out.println("SGSDG "+ t);
			System.out.println("DDDDD "+ r);
			System.out.println("sdvsd "+ mod);
			
			
		} catch (DiskControllerException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		} catch (DiskFormatException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		} /*catch (IncorrectLengthConversionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/ catch (IncorrectLengthConversionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
