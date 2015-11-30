package so.filesystem.disk;

import java.nio.ByteBuffer;

public class DiegoTestsMain {

	public static void main(String[] args) {
		byte[] arr = { 0x10, 0x25 };
		ByteBuffer wrapped = ByteBuffer.wrap(arr); // big-endian by default
		short num = wrapped.getShort(); // 1

		ByteBuffer dbuf = ByteBuffer.allocate(2);
		dbuf.putShort(num);
		byte[] bytes = dbuf.array();
		
		for (byte b : bytes) {
			System.out.println(b);
		}

	}

}
