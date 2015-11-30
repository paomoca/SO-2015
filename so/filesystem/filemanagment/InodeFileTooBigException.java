package so.filesystem.filemanagment;


public class InodeFileTooBigException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public InodeFileTooBigException(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return this.message;
	}

}
