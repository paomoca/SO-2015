import java.io.FileInputStream;
import java.io.IOException;

import so.filesystem.disk.*;
import so.filesystem.general.CONFIG;

public class FileController {
	
	
	private boolean IS_USING_CACHE = false;
	private DiskController disk;
	
	public FileController(){
		
		try {
			disk = new DiskController();
		} catch(DiskControllerException e){
			System.out.println(e.toString());
			
		}
				
	}
	
	public void readDiskFile(String fileName) throws DiskControllerException{
		
		byte[] dataBuffer = new byte[CONFIG.BLOCK_SIZE-CONFIG.CONTROL_BYTES_SIZE];
		int position;
		int blockAdressToRead = 0;
		
		//Searches for file inode reference (inodeBlockAddress) in the directory.
		int fileInodeBlock = directory.searchForFile(fileName);
		
		if(fileInodeBlock == -1){
			throw new DiskControllerException("The file: "+fileName+ " does not exist.");
		}
			
		//Asks the inode walker, based on the inodeBlockAddress for each block to read.
		while(true){
			
			dataBuffer = rawRead(blockAdressToRead, CONFIG.CONTROL_BYTES_SIZE, dataBuffer.length);
			
		}
			
	
	}
	
	
	//Pulls a file from user computer and writes it to our disk.
	public void importFile(String fileName) throws IOException{
		
		int numberOfBytesRead;
		int blockAddress;
		int blockReadCounter;
		//Temporal list storing the blocks used by this file.
		int[] listaTempo;
		
		
		FileInputStream fis = new FileInputStream(fileName);
		byte[] dataBuffer = new byte[CONFIG.BLOCK_SIZE-CONFIG.CONTROL_BYTES_SIZE];
		
		//We create a new Inode for the file.
		//Inode fileInode = new Inode();
		
		blockReadCounter = 0;
		while((numberOfBytesRead = fis.read(dataBuffer)) != -1){
			blockReadCounter++;
			
			//We ask the Free Space Manager for an available block.
			//blockAddress = fsm.firstFreeBlock();
			 
			//We write the data on the given block
			//rawWrite(blockAdress,0, dataToWrite);
			
			//We save the reference to the new address in the inode.
			//fileInode.inodeWalker(blockAddress, block);
			
			
			
		}
		
		//Write file entry into Directory.
		//Update Free Space
		//Iker nos pasa un arreglo de bytes
		
		
	}
	

}
