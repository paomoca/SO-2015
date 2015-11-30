package so.filesystem.filemanagment;
import java.io.FileInputStream;
import java.io.IOException;

import so.filesystem.disk.*;
import so.filesystem.general.CONFIG;

public class FileController {
	
	private boolean IS_USING_CACHE = false;
	private DiskController disk;
	
	public FileController(boolean cacheActive){

		this.IS_USING_CACHE = cacheActive;
		
		try {
			disk = DiskController.getInstance();
		} catch(DiskControllerException e){
			System.out.println(e.toString());
			
		}
				
	}
	
	public void readDiskFile(String fileName) throws DiskControllerException{
		
		byte[] dataBuffer = new byte[CONFIG.BLOCK_PAYLOAD_SIZE];
		int blockAdressToRead = -1;
		
		//Searches for file inode reference (inodeBlockAddress) in the directory.
		int fileInodeBlock = disk.getDirectory().searchForFile(fileName);
		
		if(fileInodeBlock == -1){
			throw new DiskControllerException("The file: "+fileName+ " does not exist.");
		}
		
		
			
		//Asks the inode walker, based on the inodeBlockAddress for each block to read.
		while(true){
			
			//Reads the inode block
			//Loops through block addresses containing the file.
			//Reads those block addresses
			
		}
			
	
	}
	
	
	//Pulls a file from user computer and writes it to our disk.
	public void importFile(String fileName) throws IOException, DiskControllerException, InodeFileTooBigException, InodeNotEnoughDiskSpaceExcepcion{
		
		int numberOfBytesRead;
		int blockAddress;

		FileInputStream fis = new FileInputStream(fileName);
		byte[] dataBuffer = new byte[CONFIG.BLOCK_PAYLOAD_SIZE];
		
		//We create a new Inode for the file.
		//TODO: QUE EL INODE LANCE EXCEPCION SI NO PUEDE
		Inode fileInode = new Inode();
		int inodeBlockAddress = fileInode.getInodeAddress();
		
		while((numberOfBytesRead = fis.read(dataBuffer)) != -1){
		
			//We ask the Free Space Manager for an available block.
			if((blockAddress = DiskFreeSpaceManager.getInstance().firstFreeBlock()) != -1){
				
				//TODO: Como definimos que hacer cuando no leimos un bloque completo. 
				//We write the data on the given block
				disk.rawWriteBlockPayload(blockAddress, dataBuffer, numberOfBytesRead);
				
				//We save the reference to the new address in the inode.
				fileInode.inodeWriteWalker(blockAddress);
				
			} else {
				
				//TODO: throw exception
			}
	
		}
		
		//If the file was created successfully we add the inode reference to the directory.
		disk.newDirectoryEntry(fileName, inodeBlockAddress);
		
		//Write file entry into Directory.
		//Update Free Space
		//Iker nos pasa un arreglo de bytes
		
		
	}
	

}
