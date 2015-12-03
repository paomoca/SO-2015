package so.filesystem.filemanagment;
import so.filesystem.disk.DiskController;
import so.filesystem.disk.DiskControllerException;
import so.filesystem.disk.DiskFreeSpaceManager;
import so.filesystem.disk.IncorrectLengthConversionException;
import so.filesystem.general.CONFIG;

public class InodeWriter {
	
	/*********************
	 * INODE INFO
	 ********************/
	
	//Must be set by whoever creates the new INODE()
	int FILE_SIZE_IN_BYTES = 0;
	
	/*********************
	 * END OF INODE INFO
	 ********************/

	int flag = 1;
	
	private int INODE_ADDRESS = -1;
	int IDB1 = -1;
	int IDB2 = -1;
	
	/* OFFSET INCREASES ON EACH INODE WALKER ITERATION AND IS USED AS A REFERENCE
	 * TO KNOW WHEN TO ALTER THE FLAG TO SWITCH BETWEEN OUR CASES. */
	int currentOffset = -1;
	
	int totalReferencedAddresses = 0;
	
	int IDB2InternalOffset = -1;
	int currentIDB2InternalIDB=-1;
	
	int currentDataAddress = -1;
	
	public InodeWriter() throws InodeNotEnoughDiskSpaceExcepcion, IncorrectLengthConversionException, DiskControllerException, InodeDirectPointerIndexOutOfRange{
		
		//We ask the Free Space Manager for an available block.
		INODE_ADDRESS = requestBlock();
		System.out.println("WRITING INODE ADDRESS: "+INODE_ADDRESS);
		
		if(CONFIG.DEBUG_SESSION){
			System.out.println("Inode Address defined: "+INODE_ADDRESS
					+ "Direct pointers: "+CONFIG.DIRECT_POINTERS);
		}
		
	}
	
	public void inodeWriteWalker(int currentDataAddress) throws InodeFileTooBigException,
	InodeNotEnoughDiskSpaceExcepcion, IncorrectLengthConversionException, DiskControllerException, InodeDirectPointerIndexOutOfRange{
		
		currentOffset++;
		this.currentDataAddress = currentDataAddress;
		
		switch(flag){
		
		case 1:
			directPointers();
			break;
		case 2:
			singleIndirectPointers();
			break;
		case 3:
			doubleIndirectPointers();
			break;
		default:		
			System.out.println("Should not exhaust");

		}	
		totalReferencedAddresses++;
	}
		
	public void directPointers() throws DiskControllerException, IncorrectLengthConversionException, InodeDirectPointerIndexOutOfRange{

		System.out.println("\nWrite direct pointer in Inode: "+currentDataAddress+" offset: "+currentOffset);
		DiskController.getInstance().writeDirectPointer(INODE_ADDRESS, currentOffset, currentDataAddress);
		
		if(currentOffset == CONFIG.DIRECT_POINTERS-1){
			flag = 2;
			resetOffset();
		}
		
	}
		
	public void singleIndirectPointers() throws InodeNotEnoughDiskSpaceExcepcion, IncorrectLengthConversionException, DiskControllerException{
		
		if (IDB1 == -1){	
			IDB1 = requestBlock();
			DiskController.getInstance().writeIDB1(INODE_ADDRESS, IDB1);
			System.out.println("--->IDB1 assigned to address: "+IDB1);
		}
		
		DiskController.getInstance().rawAddressWrite(IDB1, currentOffset, currentDataAddress);
		System.out.println("Write address: "+currentDataAddress+" to "+IDB1+" OFFSET: "+currentOffset);
		
		if(currentOffset == CONFIG.IDB_TOTAL_ADDRESSES-1){
			flag = 3;
			resetOffset();
		}
		
		
	}
	
	public void doubleIndirectPointers() throws InodeFileTooBigException, InodeNotEnoughDiskSpaceExcepcion, IncorrectLengthConversionException, DiskControllerException{
		
		if (IDB2 == -1){	
			IDB2 = requestBlock();
			DiskController.getInstance().writeIDB2(INODE_ADDRESS, IDB2);
			System.out.println("--->IDB2 assigned to address: "+IDB2);
		}
		
		if (currentIDB2InternalIDB == -1){	
			IDB2InternalOffset++;
			
			if(IDB2InternalOffset == CONFIG.IDB_TOTAL_ADDRESSES){	
				throw new InodeFileTooBigException("ERROR: File is too big to be allocated by the FileSystem.");
			}
						
			currentIDB2InternalIDB = requestBlock();
			DiskController.getInstance().rawAddressWrite(IDB2, IDB2InternalOffset, currentIDB2InternalIDB);
			// Syso. is a simulation, replace with real function.
			System.out.println("--->Write new currentIDB2InternalIDB: "+currentIDB2InternalIDB+" to: "+IDB2
					+" internal IDB2 offset"+ IDB2InternalOffset);

		}
		
		DiskController.getInstance().rawAddressWrite(currentIDB2InternalIDB, currentOffset, currentDataAddress);
		System.out.println("Write ADDRESS:"+currentDataAddress+" to internal IDB "+currentIDB2InternalIDB+" OFFSET: "+currentOffset);
		if(currentOffset == CONFIG.IDB_TOTAL_ADDRESSES-1){
			currentIDB2InternalIDB = -1;
			resetOffset();
		}	
		
	}
	
	public void resetOffset(){

		currentOffset = -1;
	}
	
	public int getInodeAddress(){
		return INODE_ADDRESS;
	}
	
	/****************************************************************
	 * USED AS AN INTERMEDIARY BETWEEN THE DISK FREE SPACE MANAGER
	 * AND ANY FUNCTION WITHIN THIS CLASS THAT ASKS FOR A FREE BLOCK
	 ****************************************************************/
	private int requestBlock() throws InodeNotEnoughDiskSpaceExcepcion{
		
		int blockAddr;
		
		if((blockAddr = DiskFreeSpaceManager.getInstance().firstFreeBlock()) == -1){
			throw new InodeNotEnoughDiskSpaceExcepcion("Not enough disk space.");
		}
		
		return blockAddr;
	}
	
	public void setFileSize(int fileSize){
		FILE_SIZE_IN_BYTES = fileSize;
	}
	
}