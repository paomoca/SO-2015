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

import java.io.IOException;
import java.util.Scanner;

public class FileSystemController {

    private FileController fileController;
    private CacheController cacheController;
    private DiskController diskController;

    public FileSystemController () {
        init();
    }

    public boolean init() {

        Scanner scanner = new Scanner(System.in);

        boolean diskFormatFlag = false;
        boolean diskContinueFlag = true;
        boolean diskFound = false;

        while (diskContinueFlag) {
            try {
                diskController = DiskController.getInstance(diskFormatFlag);
                diskContinueFlag = false;
            } catch (DiskControllerException e) {
                // TODO Auto-generated catch block
                System.out.println(e);
            } catch (DiskFormatException e) {
                // TODO Auto-generated catch block
                System.out.println(e);
                System.out
                        .print("Would you like to format the device named '"
                                + CONFIG.DISK_LOCATION
                                + "'? \n Without formatting the program will stop. [y/n]: ");
                String usrAnswer = scanner.next();
                if (usrAnswer.compareTo("y") == 0) {
                    diskFormatFlag = true;
                    diskFound = true;
                } else if (usrAnswer.compareTo("n") == 0) {
                    diskContinueFlag = false;
                    diskFound = false;
                } else {
                    System.out.println("Ok, try again!");
                }
            }
        }

        if(!diskFound){
            return  false;
        }

        boolean cacheFormatFlag = false;
        boolean cacheActive = false;
        boolean cacheContinueFlag = true;

        while (cacheContinueFlag) {
            try {
                cacheController = CacheController.getInstance(cacheFormatFlag);
                cacheActive = true;
                cacheContinueFlag = false;
            } catch (CacheControllerException e) {
                // TODO Auto-generated catch block
                System.out.println(e);
            } catch (CacheFormatException e) {
                // TODO Auto-generated catch block
                System.out.println(e);
                System.out
                        .print("Would you like to format the device named '"
                                + CONFIG.CACHE_LOCATION
                                + "' or continue with cache disabled?\n [f] -format [c] -continue: ");
                String usrAnswer = scanner.next();
                if (usrAnswer.compareTo("f") == 0) {
                    cacheFormatFlag = true;
                    cacheActive = true;
                } else if (usrAnswer.compareTo("c") == 0) {
                    cacheActive = false;
                    cacheContinueFlag = false;
                } else {
                    System.out.println("Ok, try again!");
                }
            }
        }

        fileController = new FileController(cacheActive);
        return true;

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
