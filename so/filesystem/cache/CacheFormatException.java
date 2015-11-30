package so.filesystem.cache;


public class CacheFormatException extends Exception {

	private String message;

	private static final long serialVersionUID = 1L;

	public CacheFormatException(String message) {

		this.message = message;

	}

	@Override
	public String toString() {
		return message;
	}

}
