

public class DeviceFormatException extends Exception {

	private String message;

	private static final long serialVersionUID = 1L;

	public DeviceFormatException(String message) {

		this.message = message;

	}

	@Override
	public String toString() {
		return message;
	}

}
