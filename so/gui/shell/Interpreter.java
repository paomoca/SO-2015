package so.gui.shell;

import java.io.IOException;

import so.filesystem.cache.CacheControllerException;
import so.filesystem.disk.DeviceInitializationException;
import so.filesystem.disk.DiskControllerException;
import so.filesystem.disk.DiskFreeSpaceManager;
import so.filesystem.disk.IncorrectLengthConversionException;
import so.filesystem.disk.UnidentifiedMetadataTypeException;
import so.filesystem.filemanagment.InodeDirectPointerIndexOutOfRange;
import so.filesystem.filemanagment.InodeFileTooBigException;
import so.filesystem.filemanagment.InodeNotEnoughDiskSpaceExcepcion;
import so.filesystem.general.CONFIG;
import so.filesystem.main.FileSystemController;

public class Interpreter {

	public void readCommand(String curCommand, FileSystemController fs) throws WrongCommandException, ShellAnswerException, RequestImportException, RequestExportException, RequestCreateFileExcpetion, UnidentifiedMetadataTypeException, DeviceInitializationException, CacheControllerException{
		
		String[] in = this.divideCurCommand(curCommand);
		String cmd = in[0];
		String param = in[1];
		
		// Echos param
		if(cmd.equals("echo")){
			throw new ShellAnswerException(param);
		}
		
		// Clears Shell
		else if(cmd.equals("clear")){
			throw new ShellAnswerException("\n\n\n\n\n\n\n\n\n\n\n");
		}
		
		// Says Hi to param
		else if(cmd.equals("sayshi")){
			throw new ShellAnswerException("Hi "+param);
		}
		
		// Initialize File Sysytem
//		else if(cmd.equals("initFileSystem")){
//			fs.init();
//		}
		
		// Formats Cache for File System
		else if(cmd.equals("formatCache")){
			if(fs.isCacheLoadedFlag()){
				throw new ShellAnswerException("Cache is already loaded.\nDevice: '"+CONFIG.CACHE_LOCATION+"'");
			}else{
				fs.formatCache();
			}
		}
		
		// Formats Disk for File System
		else if(cmd.equals("formatDisk")){
			//if(fs.isDiskLoadedFlag()){
				//throw new ShellAnswerException("Disk is already loaded.\nDevice: '"+CONFIG.DISK_LOCATION+"'");
			//}else{
				fs.formatDisk();
			//}
		}
		
		// Disables Cache usage
		else if(cmd.equals("dCache")){
			if(!fs.isCacheEnabledFlag()){
				throw new ShellAnswerException("Cache is already disabled.");
			}else{
				fs.disableCache();
				throw new ShellAnswerException("Cache disabled.");
			}
		}
		
		// Enables Cache usage
		else if(cmd.equals("eCache")){
			if(fs.isCacheEnabledFlag()){
				throw new ShellAnswerException("Cache is already enabled.");
			}else{
				fs.enableCache();
				throw new ShellAnswerException("Cache enabled.");
			}
		}
		
		// Requests file import from computer
		else if(cmd.equals("import")){
			if(fs.isDiskLoadedFlag()){
				if(param.equals("-1")){
					throw new ShellAnswerException("Need parameter for command: "+cmd);
				}
					throw new RequestImportException(param);
			}else{
				throw new ShellAnswerException("Could not perform action Import. No disk loaded.");
			}
		}
		
		// Read File  by name
		else if(cmd.equals("readFile")){
			if(fs.isDiskLoadedFlag()){
				if(param.equals("-1")){
					throw new ShellAnswerException("Need parameter for command: "+cmd);
				}
				throw new ShellAnswerException("TODO readFile");
			}else{
				throw new ShellAnswerException("Could not perform action Read File. No disk loaded.");
			}
		}
		
		// Random Access File by name
		else if(cmd.equals("RAFile")){
			if(fs.isDiskLoadedFlag()){
				if(param.equals("-1")){
					throw new ShellAnswerException("Need parameter for command: "+cmd);
				}
				throw new ShellAnswerException("TODO RAFile");
			}else{
				throw new ShellAnswerException("Could not perform action Random Access. No disk loaded.");
			}
		}
		
		// Delete File  by name
		else if(cmd.equals("delete")){
			if(fs.isDiskLoadedFlag()){
				if(param.equals("-1")){
					throw new ShellAnswerException("Need parameter for command: "+cmd);
				}
				try {
					fs.deleteFile(param);
				} catch (DiskControllerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IncorrectLengthConversionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InodeDirectPointerIndexOutOfRange e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InodeNotEnoughDiskSpaceExcepcion e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InodeFileTooBigException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				throw new ShellAnswerException("Deleted File: "+param);
			}else{
				throw new ShellAnswerException("Could not perform action Delete File. No disk loaded.");
			}
		}
		
		// Export File  by name
		else if(cmd.equals("export")){
			if(fs.isDiskLoadedFlag()){
				if(param.equals("-1")){
					throw new ShellAnswerException("Need parameter for command: "+cmd);
				}
				throw new RequestExportException(param);
			}else{
				throw new ShellAnswerException("Could not perform action Export File. No disk loaded.");
			}
		}
		
		// Create File by name
		else if (cmd.equals("createFile")) {
				if (param.equals("-1")) {
					throw new ShellAnswerException("Need parameter for command: " + cmd);
				}
				throw new RequestCreateFileExcpetion(param);
		}
		
		// Save File
		else if(cmd.equals("saveFile")){
			if(fs.isDiskLoadedFlag()){
				throw new ShellAnswerException("TODO saveFile");
			}else{
				throw new ShellAnswerException("Could not perform action Save File. No disk loaded.");
			}
		}
		
		// Show Free blocks
		else if(cmd.endsWith("freeB")){
			if(fs.isDiskLoadedFlag()){
				throw new ShellAnswerException("Free Blocks: "+DiskFreeSpaceManager.getInstance().getNumberFreeBlocks());
			}
			throw new ShellAnswerException("No disk");		
		}
		
		// List Disk Usage
		else if(cmd.equals("diskUsg")){
			if(fs.isDiskLoadedFlag()){
				fs.listDiskUsage();
			}else{
				throw new ShellAnswerException("Could not perform action List Disk Usage. No disk loaded.");
			}
		}
		
		// List Cache Usage
		else if (cmd.equals("cacheUsg")) {
			if (fs.isCacheLoadedFlag()&&fs.isCacheEnabledFlag()) {
				throw new ShellAnswerException("TODO cacheUsg");
			} else {
				throw new ShellAnswerException("Could not perform action List Cache Usage. Cache is disables or not loaded.");
			}
		}
		
		// List Disk Contents
		else if(cmd.equals("ls")){
			if(fs.isDiskLoadedFlag()){
				String list = fs.listDiskContents();
				throw new ShellAnswerException(list);
			}else{
				throw new ShellAnswerException("Could not perform action List Disk Contents. No disk loaded.");
			}
		}
		
		// List Cache Statistics
		else if (cmd.equals("cacheStats")) {
			if (fs.isCacheLoadedFlag()&&fs.isCacheEnabledFlag()) {
				fs.listCacheHitStatistics();
			} else {
				throw new ShellAnswerException("Could not perform action List Cache Statistics. Cache is disables or not loaded.");
			}
		}
		
		// Invalid parms case
		else if(cmd.equals("-1")&&param.equals("-1")){
			throw new ShellAnswerException("Wrong Params");
		}
		
		// Anything else is useless or wrong
		else{
			throw new WrongCommandException("Wrong Command:" + cmd);
		}
	}
	
	// Special method to send blocks to write
	public void saveFile(String fileName, FileSystemController fs) throws ShellAnswerException{
		fs.writeFile(fileName);
	}
	
	private String[] divideCurCommand(String curCommand) {

		String[] parts = curCommand.split(" ");
		
		if(parts.length==2){
			return parts;
		}else if(parts.length==1){
			String[] cmd = {parts[0],"-1"};
			return cmd;
		}else if (parts.length > 2) {
			String[] error = {"-1","-1"};
			return error;
		} else {
			String[] error = {"-1","-1"};
			return error;
		}

	}
}
