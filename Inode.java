
public class Inode {
	
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
		
		fsm = new FreeSpaceManager(500);
		IDB_TOTAL_ADDRESSES = BLOCK_SIZE/ADDRESS_SIZE;
		
	}
	
	public void inodeWriteWalker(int address){
		
		//Checar si no nos hemos terminado el total dispoibln
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
			
			System.out.println("Default ya tron� con "+totalReferencedAddresses+" direcciones.");
			System.exit(1);
			break;

		}
		
		totalReferencedAddresses++;
	}
		
	public void directPointers(){
		
		
		System.out.println("\nWrite direct pointer in Inode: "+address+" offset: "+currentOffset);
		
		
		if(currentOffset == DIRECT_POINTERS-1){
			flag = 2;
			resetOffset();
		}
		
	}
		
	public void singleIndirectPointers(){
		
		if (IDB1 == -1){	
			//Lanzar error si regresa que no hay
			IDB1 = fsm.firstFreeBlock();
			System.out.println("--->IDB1 assigned to: "+IDB1);
		}
		
		System.out.println("Write address: "+address+" to "+IDB1+" OFFSET: "+currentOffset);
		
		if(currentOffset == IDB_TOTAL_ADDRESSES-1){
			flag = 3;
			resetOffset();
		}
		
		
	}
	
	public void doubleIndirectPointers(){
		
		
		
		
		
		if (IDB2 == -1){	
			//Lanzar error si regresa que no hay
			IDB2 = fsm.firstFreeBlock();
			System.out.println("--->IDB2 assigned: "+IDB2);
		}
		
		if (currentIDB2InternalIDB == -1){	
			IDB2InternalOffset++;
			
			if(IDB2InternalOffset == IDB_TOTAL_ADDRESSES){
				
				
				System.out.println("File too big for ADDRESS: " + address);
				flag = -1;
				return;
			}
			
			//Lanzar error si regresa que no hay
			currentIDB2InternalIDB = fsm.firstFreeBlock();
			System.out.println("--->Write new currentIDB2InternalIDB: "+currentIDB2InternalIDB+" to: "+IDB2
					+" internal IDB2 offset"+ IDB2InternalOffset);

		}
		
		System.out.println("Write ADDRESS:"+address+" to internal IDB "+currentIDB2InternalIDB+" OFFSET: "+currentOffset);
		
		
		if(currentOffset == IDB_TOTAL_ADDRESSES-1){
			currentIDB2InternalIDB = -1;
			resetOffset();
		}	
		
	}
	
	public void resetOffset(){
		
		currentOffset = -1;
	}
	

}