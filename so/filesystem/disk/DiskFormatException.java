package so.filesystem.disk;


public class DiskFormatException extends Exception {

	private String message;

	private static final long serialVersionUID = 1L;

	public DiskFormatException(String message) {

		this.message = message;

	}

	@Override
	public String toString() {
		return message;
	}

}
