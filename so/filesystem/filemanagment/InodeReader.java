package so.filesystem.filemanagment;

import so.filesystem.disk.DiskController;
import so.filesystem.disk.DiskControllerException;
import so.filesystem.disk.IncorrectLengthConversionException;
import so.filesystem.general.CONFIG;

public class InodeReader {
	
	/*********************
	 * INODE INFO
	 ********************/
	
	//Must be set by whoever creates the new INODE()
	int FILE_SIZE_IN_BYTES = 0;
	
	/*********************
	 * END OF INODE INFO
	 ********************/
	
	int TOTAL_ADDRESSED_BLOCKS = 0;
	double INTERNAL_FRAGMENTATION = 0;
	int blockCount = 0;
	
	int flag = 1;
	
	private int INODE_ADDRESS = -1;
	int IDB1 = -1;
	int IDB2 = -1;
	
	/* OFFSET INCREASES ON EACH INODE WALKER ITERATION AND IS USED AS A REFERENCE
	 * TO KNOW WHEN TO ALTER THE FLAG TO SWITCH BETWEEN OUR CASES.
	 *  */
	int currentOffset = -1;
	
	int totalReferencedAddresses = 0;
	
	int IDB2InternalOffset = -1;
	int currentIDB2InternalIDB=-1;
	
	int currentDataAddress = -1;
	
	public InodeReader(int inodeBlockAddress) throws DiskControllerException, IncorrectLengthConversionException {
		
		INODE_ADDRESS = inodeBlockAddress;
		FILE_SIZE_IN_BYTES = DiskController.getInstance().readFileSize(INODE_ADDRESS);
		
		//When blockCount is equal to TOTAL_ADDRESSED_BLOCKS INTERNAL_FRAGMENTATION is taken into consideration
		TOTAL_ADDRESSED_BLOCKS = (int) Math.ceil((double)FILE_SIZE_IN_BYTES/CONFIG.BLOCK_SIZE);
		INTERNAL_FRAGMENTATION= (TOTAL_ADDRESSED_BLOCKS*CONFIG.BLOCK_SIZE)-FILE_SIZE_IN_BYTES;
		
		IDB1 = DiskController.getInstance().readIDB1(inodeBlockAddress);
		IDB2 = DiskController.getInstance().readIDB2(inodeBlockAddress);
		
	}
	
	public int inodeReadWalkerNext() throws IncorrectLengthConversionException, DiskControllerException{
		
		//Starts in 0
		currentOffset++;
		int pointer=-1;
		
		switch(flag){
		
		case 1:
			pointer = directPointers();
			break;
		case 2:
			pointer = singleIndirectPointers();
			break;
		case 3:
			pointer = doubleIndirectPointers();
			break;
		default:		
			System.out.println("Should not exhaust");

		}	
		
		//totalReferencedAddresses++;
			
		return pointer;
	}
	
	public int directPointers(){
		
		if(currentOffset == CONFIG.DIRECT_POINTERS-1){
			flag = 2;
			resetOffset();
		}
		
		return 0;
	}
	
	public int singleIndirectPointers(){
		return 0;
	}
	
	public int doubleIndirectPointers(){
		return 0;
	}
	
	public int getFileSize(){
		return FILE_SIZE_IN_BYTES;
	}
	public void resetOffset(){

		currentOffset = -1;
	}

}
