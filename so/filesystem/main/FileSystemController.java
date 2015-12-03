package so.filesystem.main;

import so.filesystem.cache.CacheController;
import so.filesystem.cache.CacheControllerException;
import so.filesystem.cache.CacheFormatException;
import so.filesystem.disk.DiskController;
import so.filesystem.disk.DiskControllerException;
import so.filesystem.disk.DiskFormatException;
import so.filesystem.disk.IncorrectLengthConversionException;
import so.filesystem.disk.UnidentifiedMetadataTypeException;
import so.filesystem.filemanagment.FileController;
import so.filesystem.filemanagment.InodeDirectPointerIndexOutOfRange;
import so.filesystem.filemanagment.InodeFileTooBigException;
import so.filesystem.filemanagment.InodeNotEnoughDiskSpaceExcepcion;
import so.filesystem.general.CONFIG;
import so.gui.shell.ShellAnswerException;

import java.io.IOException;
import java.util.Scanner;

public class FileSystemController {

    private FileController fileController;
    private CacheController cacheController;
    private DiskController diskController;
    private boolean diskLoadedFlag = false;
    private boolean cacheLoadedFlag = false;
    private boolean cacheEnabledFlag = false;

	public FileSystemController() {
    }

    public void init() throws ShellAnswerException {

    	loadDisk();
    	try{
    		loadCache();
    	}catch (ShellAnswerException e){
    		System.out.println(e.toString());
    	}
    	if(!diskLoadedFlag){
    		throw new ShellAnswerException("Error Initializing Disk :(\n");
    	}else{
			fileController = new FileController(cacheEnabledFlag);
    		if(cacheEnabledFlag){
        		throw new ShellAnswerException("FS Loaded  on disk succesfully with Cache enabled.");
    		}else{
        		throw new ShellAnswerException("FS Loaded on disk succesfully with Cache disabled.");
    		}
    	}

    }
    
    public void loadCache() throws ShellAnswerException{
    	try {
            cacheController = CacheController.getInstance(false);
            // If there are no exception cache will be considered loaded
            cacheLoadedFlag = true;
            cacheEnabledFlag = true;
        } catch (CacheControllerException e) {
            // TODO Auto-generated catch block
            System.out.println(e);
            cacheLoadedFlag = false;
            throw new ShellAnswerException(e.toString());
        } catch (CacheFormatException e) {
            // TODO Auto-generated catch block
            System.out.println(e);
            cacheLoadedFlag = false;
            throw new ShellAnswerException("Cache FileSystem unknown for: '"
                            + CONFIG.CACHE_LOCATION
                            + "'\n type formatCache to load new FileSystem on the device.");
        }
    }
    
    public void formatCache() throws ShellAnswerException{
    	try {
            cacheController = CacheController.getInstance(true);
            // If there are no exception cache will be considered loaded
            cacheLoadedFlag = true;
            throw new ShellAnswerException("Cache loaded succefully on: '"+CONFIG.CACHE_LOCATION+"'");
        } catch (CacheControllerException e) {
            // TODO Auto-generated catch block
            System.out.println(e);
            cacheLoadedFlag = false;
            throw new ShellAnswerException(e.toString());
        } catch (CacheFormatException e) {
            // TODO Auto-generated catch block
            System.out.println(e);
            cacheLoadedFlag = false;
            throw new ShellAnswerException("Error formating Cache on device: '"
                            + CONFIG.CACHE_LOCATION
                            + "'\n type formatCache to load FileSystem on the device again.");
        }
    }
    
    public void loadDisk() throws ShellAnswerException{
    	try {
			diskController = diskController.getInstance(false);
            // If there are no exception cache will be considered loaded
            diskLoadedFlag = true;
    	}catch (UnidentifiedMetadataTypeException e) {
        	throw new ShellAnswerException(e.toString());
		} catch (DiskControllerException e) {
			System.out.println(e);
            diskLoadedFlag = false;
            throw new ShellAnswerException(e.toString());
		}catch (DiskFormatException e) {
			diskLoadedFlag = false;
			if(CONFIG.DEBUG_SESSION){
				e.printStackTrace();
			}
            throw new ShellAnswerException("Disk FileSystem unknown for: '"
                            + CONFIG.DISK_LOCATION
                            + "'\n type formatDisk to load new FileSystem on the device.");
		}catch (IOException e) {
			e.printStackTrace();
        	throw new ShellAnswerException(e.toString());

		}
    }
    
    public void formatDisk() throws ShellAnswerException{
    	try {
            diskController = DiskController.getInstance(true);
            // If there are no exception cache will be considered loaded
            diskLoadedFlag = true;
            throw new ShellAnswerException("Disk loaded succefully on: '"+CONFIG.DISK_LOCATION+"'");
        } catch (DiskControllerException e) {
            System.out.println(e);
            diskLoadedFlag = false;
            throw new ShellAnswerException(e.toString());
        } catch (DiskFormatException e) {
            // TODO Auto-generated catch block
            System.out.println(e);
            diskLoadedFlag = false;
            throw new ShellAnswerException("Error formating Disk on device: '"
                            + CONFIG.DISK_LOCATION
                            + "'\n type formatDisk to load FileSystem on the device again.");
        } catch (UnidentifiedMetadataTypeException e) {
        	throw new ShellAnswerException(e.toString());
		} catch (IOException e) {
			e.printStackTrace();
        	throw new ShellAnswerException(e.toString());
		}
    }

    public void randomlyAccessFile() {

    }

    public void deleteFile() {

    }

    public void exportFile() {

    }
    
    public void readFile() {

    }
    
    public void writeFile(String fileName){
    	
    }
    
    public void importFile(String fileName) throws IncorrectLengthConversionException, InodeDirectPointerIndexOutOfRange {
        try {
            fileController.importFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DiskControllerException e) {
            e.printStackTrace();
        } catch (InodeFileTooBigException e) {
            e.printStackTrace();
        } catch (InodeNotEnoughDiskSpaceExcepcion e) {
            e.printStackTrace();
        }
    }

    public void listDiskContents() {

    }

    public void listDiskUsage() {

    }

    public void listCacheUsage() {

    }

    public void listCacheHitStatistics() {

    }

    public void enableCache() {

    }

    public void disableCache() {

    }
    
    
    // Getters & Setters for flags
    public boolean isDiskLoadedFlag() {
		return diskLoadedFlag;
	}

	public void setDiskLoadedFlag(boolean diskLoadedFlag) {
		this.diskLoadedFlag = diskLoadedFlag;
	}

	public boolean isCacheLoadedFlag() {
		return cacheLoadedFlag;
	}

	public void setCacheLoadedFlag(boolean cacheLoadedFlag) {
		this.cacheLoadedFlag = cacheLoadedFlag;
	}

	public boolean isCacheEnabledFlag() {
		return cacheEnabledFlag;
	}

	public void setCacheEnabledFlag(boolean cacheEnabledFlag) {
		this.cacheEnabledFlag = cacheEnabledFlag;
	}

}
