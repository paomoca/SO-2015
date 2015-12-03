package so.filesystem.filemanagment;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import so.filesystem.cache.CacheController;
import so.filesystem.cache.CacheControllerException;
import so.filesystem.cache.CacheFormatException;
import so.filesystem.disk.*;
import so.filesystem.general.CONFIG;
import so.gui.shell.ShellAnswerException;

public class FileController {
	
	private boolean IS_USING_CACHE = false;
	private DiskController disk;
	private CacheController cache;
	private DiskDirectory directory;
	
	public FileController(boolean cacheActive){

		this.IS_USING_CACHE = cacheActive;
		
		try {
			disk = DiskController.getInstance();
			directory = new DiskDirectory();
		} catch(DiskControllerException e){
			System.out.println(e.toString());		
		}
		
		//Instance CacheController
		if (this.IS_USING_CACHE == true) {
			try {
				this.cache = CacheController.getInstance();
			} catch (CacheControllerException e) {
				// TODO Auto-generated catch block
				this.IS_USING_CACHE = false;
				e.printStackTrace();
			}
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
			
			blockAdressToRead = inode.inodeReadWalkerNext(false);
			
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
	
	public void exportFile(String fileName, String filePath) throws DiskControllerException, IncorrectLengthConversionException, InodeDirectPointerIndexOutOfRange, InodeNotEnoughDiskSpaceExcepcion, InodeFileTooBigException, IOException{
		
		int blockAdressToRead = -1;
		int blocksRead = 0;
		
//		if(!directory.isFileInDirectory(fileName)){
//			throw new ShellAnswerException("");
//		}
		
		//Searches for file inode reference (inodeBlockAddress) in the directory.
		int fileInodeBlock = directory.searchForFile(fileName);
//		
		if(fileInodeBlock == -1){
			throw new DiskControllerException("The file: "+fileName+ " does not exist.");
		}
		InodeReader inode = new InodeReader(fileInodeBlock);
		
		int totalBlocksToAddress = inode.getTotalBlocksToAddress();
		System.out.println("TOTAL BLOCKS TO ADDRESS: "+totalBlocksToAddress);
		int internalFragmentation = inode.getInternalFragmentation();
		System.out.println("INTERNAL FRAGMENTATION: "+internalFragmentation);
		
		byte[] dataBuffer = new byte[CONFIG.BLOCK_PAYLOAD_SIZE];
		byte[] lastBlockDataBuffer = new byte[CONFIG.BLOCK_PAYLOAD_SIZE-internalFragmentation];
		
		//We get a pointer to the file to export to.
		FileOutputStream fis = new FileOutputStream(filePath);
		
		//Asks the inode walker, based on the inodeBlockAddress for each block to read.
		while(blocksRead < totalBlocksToAddress){
			
			blockAdressToRead = inode.inodeReadWalkerNext(false);
			System.out.println("BLOCK ADDRESS TO READ "+blockAdressToRead);
			
			//If we are dealing with the last block.
			if(blocksRead == totalBlocksToAddress-1){
				lastBlockDataBuffer = DiskController.getInstance().rawReadBlockPayload(blockAdressToRead,lastBlockDataBuffer.length);
				fis.write(lastBlockDataBuffer);
				System.out.println("WROTE LAST BLOCK");
				
			} else {
				dataBuffer = DiskController.getInstance().rawReadBlockPayload(blockAdressToRead);
				fis.write(dataBuffer);
			
			}
			
			blocksRead++;
			
		}
			
		System.out.println("Total blocks actually read "+blocksRead);
		fis.close();
		
		
	}
	
	
	//Pulls a file from user computer and writes it to our disk.
	public void importFile(String filePath, String fileName) throws IOException, DiskControllerException, InodeFileTooBigException, InodeNotEnoughDiskSpaceExcepcion, IncorrectLengthConversionException, InodeDirectPointerIndexOutOfRange, ShellAnswerException{
		
		int numberOfBytesRead;
		int blockAddress;
		int totalBytesRead=0;

		FileInputStream fis = new FileInputStream(filePath);
		byte[] dataBuffer = new byte[CONFIG.BLOCK_PAYLOAD_SIZE];
		
		//TODO: CREAMOS UNA ENTRADA EN EL DIRECTORIO
		if(directory.isFileInDirectory(fileName)){
			throw new ShellAnswerException("File "+fileName+" is already in Disk.");
		}
		
		//We create a new Inode for the file. The Inode itself requests a free block.
		InodeWriter inode = new InodeWriter();
		int inodeBlockAddress = inode.getInodeAddress();
		
		directory.newFile(fileName, inodeBlockAddress);
		
		while((numberOfBytesRead = fis.read(dataBuffer)) != -1){
			
		
			totalBytesRead += numberOfBytesRead;
			//We ask the Free Space Manager for an available block.
			if((blockAddress = DiskFreeSpaceManager.getInstance().firstFreeBlock()) != -1){
				System.out.println("BLOCK ADDRESS ASIGNED: "+blockAddress);
				
				//decidirsiescribir
				DiskController.getInstance().rawWriteBlockPayload(blockAddress, dataBuffer, numberOfBytesRead);
				
				//Sets control bytes.
				DiskController.getInstance().setBlockAccessFrequency(blockAddress, 0);
				DiskController.getInstance().setDeduplicationCounter(blockAddress, 0);
				
				inode.inodeWriteWalker(blockAddress);
				
			} else {
				
				fis.close();
				//TODO: throw exception de que ya no hay espacio
				
			}
	
		}
		
		DiskController.getInstance().writeFileSize(inodeBlockAddress, totalBytesRead);
		System.out.println("(IMPORT) TOTAL FILE SIZE READ IN BYTES: "+DiskController.getInstance().readFileSize(inodeBlockAddress));
		
		//If the file was created successfully we add the inode reference to the directory.
		//disk.newDirectoryEntry(fileName, inodeBlockAddress);
		
		fis.close();
		System.out.println("Inode block address assigned: "+inodeBlockAddress);
		//directory.newFile(fileName, inodeBlockAddress);
		
	}
	
	
	public void deleteFile(String fileName, String filePath) throws DiskControllerException, IncorrectLengthConversionException, InodeDirectPointerIndexOutOfRange, InodeNotEnoughDiskSpaceExcepcion, InodeFileTooBigException, IOException{
		
		int blockAdressToRead = -1;
		int blocksRead = 0;
		
		//Searches for file inode reference (inodeBlockAddress) in the directory.
		int fileInodeBlock = directory.searchForFile(fileName);
//		
		if(fileInodeBlock == -1){
			throw new DiskControllerException("The file: "+fileName+ " does not exist.");
		}
		//TODO: supuestamente le pasamos lo que nos de el directorio
		InodeReader inode = new InodeReader(fileInodeBlock);
		
		int totalBlocksToAddress = inode.getTotalBlocksToAddress();
		System.out.println("TOTAL BLOCKS TO ADDRESS: "+totalBlocksToAddress);
		
		//We get a pointer to the file to export to.
		FileOutputStream fis = new FileOutputStream(filePath);
		
		//Asks the inode walker, based on the inodeBlockAddress for each block to read.
		while(blocksRead < totalBlocksToAddress){
			
			blockAdressToRead = inode.inodeReadWalkerNext(true);
			System.out.println("BLOCK ADDRESS TO DELETE "+blockAdressToRead);

			DiskFreeSpaceManager.getInstance().freeBlocks(blockAdressToRead);
			blocksRead++;
			
		}
		
		DiskFreeSpaceManager.getInstance().freeBlocks(inode.getIDB1());
		DiskFreeSpaceManager.getInstance().freeBlocks(inode.getIDB2());
		DiskFreeSpaceManager.getInstance().freeBlocks(fileInodeBlock);
		
		System.out.println("Total blocks actually freed "+blocksRead);
		fis.close();
		
		
	}
	
	
	
	public void enableCache() {
		try {
			this.cache = CacheController.getInstance();
			this.IS_USING_CACHE = true;
		} catch (CacheControllerException e) {
			// TODO Auto-generated catch block
			this.IS_USING_CACHE = false;
			e.printStackTrace();
		}
	}
	
	public void disableCache() {
		this.IS_USING_CACHE = false;
	}

	public DiskDirectory getDirectory() {
		return directory;
	}

	public void setDirectory(DiskDirectory directory) {
		this.directory = directory;
	}

}
