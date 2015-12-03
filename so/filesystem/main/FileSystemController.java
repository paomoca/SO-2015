package so.filesystem.main;

import so.filesystem.cache.CacheController;
import so.filesystem.cache.CacheControllerException;
import so.filesystem.cache.CacheFormatException;
import so.filesystem.disk.DeviceInitializationException;
import so.filesystem.disk.DiskController;
import so.filesystem.disk.DiskControllerException;
import so.filesystem.disk.DiskFormatException;
import so.filesystem.disk.DiskFreeSpaceManager;
import so.filesystem.disk.IncorrectLengthConversionException;
import so.filesystem.disk.UnidentifiedMetadataTypeException;
import so.filesystem.filemanagment.FileController;
import so.filesystem.filemanagment.InodeDirectPointerIndexOutOfRange;
import so.filesystem.filemanagment.InodeFileTooBigException;
import so.filesystem.filemanagment.InodeNotEnoughDiskSpaceExcepcion;
import so.filesystem.general.CONFIG;
import so.filesystem.general.Statistics;
import so.gui.shell.ShellAnswerException;

import java.io.IOException;
import java.util.ArrayList;

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
            fileController.getDirectory().resetDirectory();
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

    public void deleteFile(String fileName) throws DiskControllerException, IncorrectLengthConversionException, InodeDirectPointerIndexOutOfRange, InodeNotEnoughDiskSpaceExcepcion, InodeFileTooBigException, IOException, ShellAnswerException, UnidentifiedMetadataTypeException, DeviceInitializationException {
    	fileController.deleteFile(fileName);
    	ArrayList<byte[]> fsmList = DiskFreeSpaceManager.getInstance().updateFreeSpace();
		DiskController.getInstance().rawMetadataWrite(fsmList, "FSM_BITMAP");
    }

    public void exportFile(String fileName, String filePath) throws DiskControllerException, IncorrectLengthConversionException, InodeDirectPointerIndexOutOfRange, InodeNotEnoughDiskSpaceExcepcion, InodeFileTooBigException, IOException, ShellAnswerException, CacheControllerException {
    	System.out.println("export");
    	fileController.exportFile(fileName, filePath);
    	
    }
    
    public void readFile() {
    	
    }
    
    public void writeFile(String fileName) throws ShellAnswerException{
    	throw new ShellAnswerException("TODO");
    }
    
    public void importFile(String filePath, String fileName) throws IncorrectLengthConversionException, InodeDirectPointerIndexOutOfRange, ShellAnswerException, UnidentifiedMetadataTypeException, DeviceInitializationException {
        try {
        	fileController.importFile(filePath,fileName);
        	ArrayList<byte[]> fsmList = DiskFreeSpaceManager.getInstance().updateFreeSpace();
    		DiskController.getInstance().rawMetadataWrite(fsmList, "FSM_BITMAP");
        	System.out.println("import");
//        	ArrayList<String> list = fileController.getDirectory().listDirectory();
//        	for (String file : list) {
//				System.out.println(file);
//			}
        	fileController.getDirectory().saveDirectory();
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

    public String listDiskContents() {
    	String ls = "";
    	ArrayList<String> list = fileController.getDirectory().listDirectory();
    	if(list.size()<=0){
    		return "Directory Empty";
    	}
    	int i = 0;
    	for (String file : list) {
			ls = ls + "  "+file+"  ";
			i++;
			if(i%3==0){
				ls = ls+"\n";
			}
		}
    	return ls;
    }

    public void listDiskUsage() {
    	
    }

    public void listCacheUsage() throws ShellAnswerException {
    	Statistics stats = new Statistics();
    	String s = "";
    	ArrayList<String> st = stats.getAllDiskStatistics();
    	for (String stat : st) {
			s = s + stat + "\n";
		}
    	throw new ShellAnswerException(s);
    }

    public void listCacheHitStatistics() throws ShellAnswerException {
    	Statistics stats = new Statistics();
    	String s = "";
    	ArrayList<String> st = stats.getAllCacheStatistics();
    	for (String stat : st) {
			s = s + stat + "\n";
		}
    	throw new ShellAnswerException(s);
    }

    public void enableCache() throws ShellAnswerException{
    	loadCache();
    	fileController.enableCache();
    }

    public void disableCache() {
    	cacheController = null;
    	fileController.disableCache();
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
