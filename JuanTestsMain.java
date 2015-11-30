import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class JuanTestsMain {

	private static Scanner scan;
	private static CacheController cc;

	public static void main(String[] args) {

		
		
		scan = new Scanner(System.in);
		// System.out.println("Escribe algo: ");
		// String s = scan.next();
		// System.out.println(s);

		String deviceName = "cacheRawDeviceTestFile";
		boolean formatFlag = false;
		boolean cacheActive = false;
		boolean continueFlag = true;

		while (continueFlag) {
			try {
				cc = new CacheController(deviceName, 2, 0,
						formatFlag);
				continueFlag = false;
			} catch (CacheControllerException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
				System.out.println("Some shit happend");
			} catch (DeviceFormatException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
				System.out
						.print("Would you like to format the device named '"
								+ deviceName
								+ "' or continue with cache disabled? [f] -format [c] -continue: ");
				String usrAnswer = scan.next();
				if (usrAnswer.compareTo("f") == 0) {
					formatFlag = true;
				} else if (usrAnswer.compareTo("c") == 0) {
					formatFlag = false;
					continueFlag = false;
				} else {
					System.out.println("Ok, try again!");
				}
			}
		}
		

		
	}

}
