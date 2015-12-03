package so.filesystem.filemanagment;

import so.filesystem.disk.DiskController;
import so.filesystem.disk.DiskControllerException;
import so.filesystem.disk.IncorrectLengthConversionException;
import so.filesystem.general.CONFIG;


/**
 * The InodeReader will receive an inode block address and will traverse the structure in order
 * to return, when requested, the next address to read. It's only job is to return block addresses,
 * no actual data is being read here apart from block addresses.
 */
public class InodeReader {
	
	
	/**** INODE INFO ****/
	//Must be set by whoever created the new InodeWriter()
	
	int FILE_SIZE_IN_BYTES = 0;
	
	/**** END OF INODE INFO ****/
	
	//Given the file size we will determine the amount of blocks to read.
	int TOTAL_BLOCKS_TO_ADDRESS = 0;
	//Last block offset, given by internal fragmentation value.
	int INTERNAL_FRAGMENTATION = 0;
	
	//Defines whether to treat direct, single indirect or double indirect pointers.
	int flag = 1;
	
	private int INODE_ADDRESS = -1;
	int IDB1 = -1;
	int IDB2 = -1;
	
	//Offset within the list of double indirect pointers.
	int IDB2InternalOffset = -1;
	//Offset within the last level of double indirect pointers.
	int currentIDB2InternalIDB=-1;
	
	/* OFFSET INCREASES ON EACH INODE WALKER ITERATION AND IS USED AS A REFERENCE
	 * TO KNOW WHEN TO ALTER THE FLAG TO SWITCH BETWEEN OUR CASES * */
	int currentOffset = -1;

	//Block addressed to be returned by the inodeReadWalker when requested.
	int currentDataAddress = -1;
	
	public InodeReader(int inodeBlockAddress) throws DiskControllerException, IncorrectLengthConversionException {
		
		INODE_ADDRESS = inodeBlockAddress;
		System.out.println("Reading Inode Address: "+inodeBlockAddress);
		
		//Through the file size in bytes we can determine the amount of blocks that should be read.
		FILE_SIZE_IN_BYTES = DiskController.getInstance().readFileSize(INODE_ADDRESS);
		
		//When blockCount is equal to TOTAL_BLOCKS_TO_ADDRESS INTERNAL_FRAGMENTATION is taken into consideration.
		//This values are important to whoever is using the InodeReader. They are used as a reference to know when 
		//to stop asking for addresses and when to treat the last block differently (given the internal fragmentation).
		TOTAL_BLOCKS_TO_ADDRESS = (int) Math.ceil((double)FILE_SIZE_IN_BYTES/CONFIG.BLOCK_SIZE);
		INTERNAL_FRAGMENTATION= (TOTAL_BLOCKS_TO_ADDRESS*CONFIG.BLOCK_SIZE)-FILE_SIZE_IN_BYTES;
		
	}
	
	public int inodeReadWalkerNext() throws IncorrectLengthConversionException, DiskControllerException, InodeDirectPointerIndexOutOfRange, InodeNotEnoughDiskSpaceExcepcion, InodeFileTooBigException{
		
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
			
		return pointer;
	}
	
	private int directPointers() throws DiskControllerException, IncorrectLengthConversionException, InodeDirectPointerIndexOutOfRange{

		System.out.println("\nRead direct pointer in Inode: "+INODE_ADDRESS+" offset: "+currentOffset);
		int pointer = DiskController.getInstance().readDirectPointer(INODE_ADDRESS, currentOffset);
		
		if(currentOffset == CONFIG.DIRECT_POINTERS-1){
			flag = 2;
			resetOffset();
		}
		
		return pointer;
	}
		
	private int singleIndirectPointers() throws DiskControllerException, IncorrectLengthConversionException {
		
		if (IDB1 == -1){	
			IDB1 = DiskController.getInstance().readIDB1(INODE_ADDRESS);
			System.out.println("--->IDB1 assigned to address: "+IDB1);
		}
		
		int pointer = DiskController.getInstance().rawAddressRead(IDB1, currentOffset);
		System.out.println("Read address: "+currentDataAddress+" to "+IDB1+" OFFSET: "+currentOffset);
		
		if(currentOffset == CONFIG.IDB_TOTAL_ADDRESSES-1){
			flag = 3;
			resetOffset();
		}
		
		return pointer;
	}
	
	private int doubleIndirectPointers() throws InodeFileTooBigException, InodeNotEnoughDiskSpaceExcepcion, IncorrectLengthConversionException, DiskControllerException{
		
		if (IDB2 == -1){	
			
			IDB2 = DiskController.getInstance().readIDB2(INODE_ADDRESS);
			System.out.println("--->IDB2 assigned to address: "+IDB2);
		}
		
		if (currentIDB2InternalIDB == -1){	
			IDB2InternalOffset++;
					
			currentIDB2InternalIDB = DiskController.getInstance().rawAddressRead(IDB2, IDB2InternalOffset);
			// Syso. is a simulation, replace with real function.
			System.out.println("--->Read new currentIDB2InternalIDB: "+currentIDB2InternalIDB+" to: "+IDB2
					+" internal IDB2 offset"+ IDB2InternalOffset);

		}
		
		int pointer = DiskController.getInstance().rawAddressRead(currentIDB2InternalIDB, currentOffset);
		System.out.println("Read ADDRESS:"+currentDataAddress+" to internal IDB "+currentIDB2InternalIDB+" OFFSET: "+currentOffset);
		if(currentOffset == CONFIG.IDB_TOTAL_ADDRESSES-1){
			currentIDB2InternalIDB = -1;
			resetOffset();
		}	
		
		return pointer;
	}
	private void resetOffset(){

		currentOffset = -1;
	}

	public int getFileSize(){
		return FILE_SIZE_IN_BYTES;
	}
	
	public int getTotalBlocksToAddress(){
		return TOTAL_BLOCKS_TO_ADDRESS;
	}
	
	public int getInternalFragmentation(){
		return INTERNAL_FRAGMENTATION;
	}

}
