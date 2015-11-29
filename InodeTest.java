
public class InodeTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Inode fileInode = new Inode();
		
		for(int i = 1001; i < 1115 ; i++){
			
			fileInode.inodeWriteWalker(i);
			
			
		}

	}

}
