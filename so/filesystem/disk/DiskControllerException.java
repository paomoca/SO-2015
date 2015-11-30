package so.filesystem.disk;

public class DiskControllerException extends Exception{

	private String message;
	
	private static final long serialVersionUID = 1L;
	
	public DiskControllerException(String message){
		
		this.message = message;
		
	}
	
	@Override
	public String toString() {
		return message;
	}

}
