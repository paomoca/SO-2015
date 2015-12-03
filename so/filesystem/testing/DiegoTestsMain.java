package so.filesystem.testing;

import java.io.IOException;

import so.filesystem.disk.DiskController;
import so.filesystem.disk.DiskControllerException;
import so.filesystem.disk.DiskFormatException;
import so.filesystem.disk.DiskFreeSpaceManager;
import so.filesystem.disk.IncorrectLengthConversionException;
import so.filesystem.disk.UnidentifiedMetadataTypeException;
import so.filesystem.filemanagment.FileController;
import so.filesystem.filemanagment.InodeDirectPointerIndexOutOfRange;
import so.filesystem.filemanagment.InodeFileTooBigException;
import so.filesystem.filemanagment.InodeNotEnoughDiskSpaceExcepcion;
import so.filesystem.filemanagment.InodeReader;
import so.filesystem.general.CONFIG;

public class DiegoTestsMain {

	public static void main(String[] args) throws DiskControllerException, DiskFormatException, UnidentifiedMetadataTypeException, IOException, InodeFileTooBigException, InodeNotEnoughDiskSpaceExcepcion, IncorrectLengthConversionException, InodeDirectPointerIndexOutOfRange {

		System.out.println("test");
		boolean skipped_debug = false;
		if(CONFIG.DEBUG_SESSION){
			CONFIG.DEBUG_SESSION = false;
			skipped_debug = true;
		}
		DiskController dc2 = DiskController.getInstance(true);
		System.out.println("Free blocks antes"+DiskFreeSpaceManager.getInstance().getNumberFreeBlocks());
		if(skipped_debug){
			CONFIG.DEBUG_SESSION = true;
		}
		
		FileController fileController = new FileController(false);
		fileController.importFile("Interestellar-original.mp4");
		//fileController.importFile("UserManual.pdf");
		//fileController.exportFile("funciona.pdf");
		//fileController.exportFile("pruebaplisfunciona.txt");
		
//		InodeReader inodeR = new InodeReader(1);
//		
//		for(int i = 0; i< 400; i++){
//			System.out.println(i+" "+inodeR.inodeReadWalkerNext());
//		}
		
		System.out.println("Free blocks despues"+DiskFreeSpaceManager.getInstance().getNumberFreeBlocks());
		
		dc2.finalize();
		
		
	}

}
