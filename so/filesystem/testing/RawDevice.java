package so.filesystem.testing;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RawDevice {
	

	public static void main(String[] args) {
		
		RandomAccessFile randomAccessFile;
		String string = "hola";
		byte[] bufferRead = new byte[string.length()];
		
		try {
			
			randomAccessFile = new RandomAccessFile("/dev/sdb1", "rw");
			//randomAccessFile.seek(50);
			byte[] bufferWrite = string.getBytes();
			randomAccessFile.read(bufferWrite);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
try {
			
			randomAccessFile = new RandomAccessFile("/dev/sdb1", "rw");
			//randomAccessFile.seek(50);
			byte[] bufferWrite = string.getBytes();
			randomAccessFile.read(bufferWrite);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
