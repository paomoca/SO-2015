package so.filesystem.filemanagment;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

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
	
	public void readDiskFile(String fileName) throws DiskControllerException, IncorrectLengthConversionException, InodeDirectPointerIndexOutOfRange, InodeNotEnoughDiskSpaceExcepcion, InodeFileTooBigException{
		
		int blockAdressToRead = -1;
		int blocksRead = 0;
		
		//Searches for file inode reference (inodeBlockAddress) in the directory.
		int fileInodeBlock = disk.getDirectory().searchForFile(fileName);
		
		if(fileInodeBlock == -1){
			throw new DiskControllerException("The file: "+fileName+ " does not exist.");
		}
		
		InodeReader inode = new InodeReader(fileInodeBlock);
		int totalBlocksToAddress = inode.getTotalBlocksToAddress();
		int internalFragmentation = inode.getInternalFragmentation();
		
		byte[] dataBuffer = new byte[CONFIG.BLOCK_PAYLOAD_SIZE];
		byte[] lastBlockDataBuffer = new byte[CONFIG.BLOCK_PAYLOAD_SIZE-internalFragmentation];
		
		//Asks the inode walker, based on the inodeBlockAddress for each block to read.
		while(blocksRead < totalBlocksToAddress){
			
			blockAdressToRead = inode.inodeReadWalkerNext();
			
			if(blocksRead == totalBlocksToAddress-1){
				lastBlockDataBuffer = DiskController.getInstance().rawReadBlockPayload(blockAdressToRead,lastBlockDataBuffer.length);
				System.out.println(new String(lastBlockDataBuffer));
			} else {
				dataBuffer = DiskController.getInstance().rawReadBlockPayload(blockAdressToRead);
				System.out.println(new String(dataBuffer));
			}
			
			blocksRead++;
			
		}
			
	
	}
	
	
	//Pulls a file from user computer and writes it to our disk.
	public void importFile(String fileName) throws IOException, DiskControllerException, InodeFileTooBigException, InodeNotEnoughDiskSpaceExcepcion, IncorrectLengthConversionException, InodeDirectPointerIndexOutOfRange{
		
		int numberOfBytesRead;
		int blockAddress;

		FileInputStream fis = new FileInputStream(fileName);
		byte[] dataBuffer = new byte[CONFIG.BLOCK_PAYLOAD_SIZE];
		
		//TODO: CREAMOS UNA ENTRADA EN EL DIRECTORIO
		
		//We create a new Inode for the file. The Inode itself requests a free block.
		InodeWriter inode = new InodeWriter();
		int inodeBlockAddress = inode.getInodeAddress();
		
		while((numberOfBytesRead = fis.read(dataBuffer)) != -1){
		
			//We ask the Free Space Manager for an available block.
			if((blockAddress = DiskFreeSpaceManager.getInstance().firstFreeBlock()) != -1){
				
				blockAddress = DiskFreeSpaceManager.getInstance().firstFreeBlock();
				DiskController.getInstance().rawWriteBlockPayload(inode.getInodeAddress(), dataBuffer, numberOfBytesRead);
				
				//Sets control bytes.
				DiskController.getInstance().setBlockAccessFrequency(inode.getInodeAddress(), 0);
				DiskController.getInstance().setDeduplicationCounter(inode.getInodeAddress(), 0);
				
				inode.inodeWriteWalker(blockAddress);
				
				
				
				//TODO: Como definimos que hacer cuando no leimos un bloque completo. 
				//We write the data on the given block
				//disk.rawWriteBlockPayload(blockAddress, dataBuffer, numberOfBytesRead);
				
				//We save the reference to the new address in the inode.
				//fileInode.inodeWriteWalker(blockAddress);
				
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
