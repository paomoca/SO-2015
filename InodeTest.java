
public class InodeTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Inode fileInode = new Inode();
		
		for(int i = 2001; i < 2115 ; i++){
			
			fileInode.inodeWriteWalker(i);
			// Que hay
			
		}

	}

}
