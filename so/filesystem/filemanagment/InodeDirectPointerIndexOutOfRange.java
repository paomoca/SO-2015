package so.filesystem.filemanagment;

public class InodeDirectPointerIndexOutOfRange extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public InodeDirectPointerIndexOutOfRange(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return this.message;
	}

}

