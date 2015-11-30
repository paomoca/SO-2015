
public class InodeTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Inode fileInode = new Inode();
		
		for(int i = 2001; i < 2115 ; i++){
			
			try {
				fileInode.inodeWriteWalker(i);
			} catch (InodeFileTooBigException | InodeNotEnoughDiskSpaceExcepcion e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Hola lo edite lo edito pao

			// Que hay

			
		}

	}

}
