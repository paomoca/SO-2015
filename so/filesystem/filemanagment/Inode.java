package so.filesystem.filemanagment;
import so.filesystem.disk.DiskFreeSpaceManager;
import so.filesystem.general.FreeSpaceManager;

public class Inode {
	
	private int inodeAddress = -1;
	
	private int BLOCK_SIZE = 40;
	private int ADDRESS_SIZE = 4;
	private int DIRECT_POINTERS = 2;
	private int IDB_TOTAL_ADDRESSES;
	
	int totalReferencedAddresses = 0;
		
	int IDB1 = -1;
	int IDB2 = -1;
	
	int IDB2InternalOffset = -1;
	int currentIDB2InternalIDB=-1;
	
	int currentOffset = -1;
	
	int flag = 1;
	FreeSpaceManager fsm;
	int address = -1;
	
	
	public Inode(){
		
		//We ask the Free Space Manager for an available block.
		if((inodeAddress = DiskFreeSpaceManager.getInstance().firstFreeBlock()) == -1){
			
			//TODO: THROW EXCEPTION
		}
		
		fsm = DiskFreeSpaceManager.getInstance(500);
		IDB_TOTAL_ADDRESSES = BLOCK_SIZE/ADDRESS_SIZE;
		
	}
	
	public void inodeWriteWalker(int address) throws InodeFileTooBigException, InodeNotEnoughDiskSpaceExcepcion{
		
		currentOffset++;
		this.address = address;
		
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
		
	public void directPointers(){
		
		// Syso. is a simulation, replace with real function.
		System.out.println("\nWrite direct pointer in Inode: "+address+" offset: "+currentOffset);
		
		if(currentOffset == DIRECT_POINTERS-1){
			flag = 2;
			resetOffset();
		}
		
	}
		
	public void singleIndirectPointers(){
		
		if (IDB1 == -1){	
			if ((IDB1 = fsm.firstFreeBlock()) == -1) {
				// Throw not enough disk space exception.
			}
			// Syso. is a simulation, replace with real function.
			System.out.println("--->IDB1 assigned to: "+IDB1);
		}
		
		// Syso. is a simulation, replace with real function.
		System.out.println("Write address: "+address+" to "+IDB1+" OFFSET: "+currentOffset);
		
		if(currentOffset == IDB_TOTAL_ADDRESSES-1){
			flag = 3;
			resetOffset();
		}
		
		
	}
	
	public void doubleIndirectPointers() throws InodeFileTooBigException, InodeNotEnoughDiskSpaceExcepcion{
		
		if (IDB2 == -1){	
			if ((IDB2 = fsm.firstFreeBlock()) == -1) {
				throw new InodeNotEnoughDiskSpaceExcepcion("ERROR: There's not enough free space in the disk to write the file.");
			}
			// Syso. is a simulation, replace with real function.
			System.out.println("--->IDB2 assigned: "+IDB2);
		}
		
		if (currentIDB2InternalIDB == -1){	
			IDB2InternalOffset++;
			
			if(IDB2InternalOffset == IDB_TOTAL_ADDRESSES){	
				throw new InodeFileTooBigException("ERROR: File is too big to be allocated by the FileSystem.");
			}
						
			if ((currentIDB2InternalIDB = fsm.firstFreeBlock()) == -1) {
				throw new InodeNotEnoughDiskSpaceExcepcion("ERROR: There's not enough free space in the disk to write the file.");
			}
			// Syso. is a simulation, replace with real function.
			System.out.println("--->Write new currentIDB2InternalIDB: "+currentIDB2InternalIDB+" to: "+IDB2
					+" internal IDB2 offset"+ IDB2InternalOffset);

		}
		// Syso. is a simulation, replace with real function.
		System.out.println("Write ADDRESS:"+address+" to internal IDB "+currentIDB2InternalIDB+" OFFSET: "+currentOffset);
		if(currentOffset == IDB_TOTAL_ADDRESSES-1){
			currentIDB2InternalIDB = -1;
			resetOffset();
		}	
		
	}
	
	public void resetOffset(){

		currentOffset = -1;
	}
	
	public int getInodeAddress(){
		return inodeAddress;
	}
	

}