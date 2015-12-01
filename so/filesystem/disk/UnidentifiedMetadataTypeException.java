package so.filesystem.disk;

public class UnidentifiedMetadataTypeException extends Exception {
	
	private String message;
	
	private static final long serialVersionUID = 1L;
	
	public UnidentifiedMetadataTypeException(String message){
		
		this.message = message;
		
	}
	
	@Override
	public String toString() {
		return message;
	}
}