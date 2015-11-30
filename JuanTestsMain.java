
import java.util.Scanner;

import so.filesystem.disk.DiskController;
import so.filesystem.disk.DiskControllerException;
import so.filesystem.disk.DiskFormatException;
import so.filesystem.general.CONFIG;

public class JuanTestsMain {

	private static Scanner scan;
	private static CacheController cc;
	private static DiskController dc;

	public static void main(String[] args) {
		scan = new Scanner(System.in);
		// System.out.println("Escribe algo: ");
		// String s = scan.next();
		// System.out.println(s);

		String cacheName = "cacheRawDeviceTestFile";
		boolean cacheFormatFlag = false;
		boolean cacheActive = false;
		boolean cacheContinueFlag = true;

		while (cacheContinueFlag) {
			try {
				cc = new CacheController(cacheName, 2, 0,
						cacheFormatFlag);
				cacheContinueFlag = false;
			} catch (CacheControllerException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
				System.out.println("Some shit happend");
			} catch (DeviceFormatException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
				System.out
						.print("Would you like to format the device named '"
								+ cacheName
								+ "' or continue with cache disabled? [f] -format [c] -continue: ");
				String usrAnswer = scan.next();
				if (usrAnswer.compareTo("f") == 0) {
					cacheFormatFlag = true;
				} else if (usrAnswer.compareTo("c") == 0) {
					cacheActive = false;
					cacheContinueFlag = false;
				} else {
					System.out.println("Ok, try again!");
				}
			}
		}
		
		String diskName = CONFIG.DISK_LOCATION;
		boolean diskFormatFlag = false;
		boolean diskContinueFlag = true;
		
		while (diskContinueFlag) {
			try {
				dc = new DiskController(diskFormatFlag);
				diskContinueFlag = false;
			} catch (DiskControllerException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
				System.out.println("Some shit happend");
			} catch (DiskFormatException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
				System.out
						.print("Would you like to format the device named '"
								+ diskName
								+ "'? \n Without formatting the program will stop. [y/n]: ");
				String usrAnswer = scan.next();
				if (usrAnswer.compareTo("y") == 0) {
					diskFormatFlag = true;
				} else if (usrAnswer.compareTo("n") == 0) {
					diskContinueFlag = false;
				} else {
					System.out.println("Ok, try again!");
				}
			}
		}
		
		
		
	}

}
