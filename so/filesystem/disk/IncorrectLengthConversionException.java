package so.filesystem.disk;

public class IncorrectLengthConversionException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public IncorrectLengthConversionException(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return this.message;
	}
}
