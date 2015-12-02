package so.filesystem.main;

import so.filesystem.cache.CacheController;
import so.filesystem.cache.CacheControllerException;
import so.filesystem.cache.CacheFormatException;
import so.filesystem.disk.DiskController;
import so.filesystem.disk.DiskControllerException;
import so.filesystem.disk.DiskFormatException;
import so.filesystem.filemanagment.FileController;
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
        //init();
    }

    public void init() throws ShellAnswerException {

    	//loadDisk();
    	loadCache();
    	if(!cacheLoadedFlag){
    		throw new ShellAnswerException("Error Initializing :(\n");
    	}
    	fileController = new FileController(cacheEnabledFlag);
    	throw new ShellAnswerException("FS Load SuccesFull!");

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
    
    public void loadDisk(){
    	
    }

    public void randomlyAccessFile() {

    }

    public void deleteFile() {

    }

    public void exportFile() {

    }

    public void importFile(String fileName) {
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

}
