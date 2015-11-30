package so.filesystem.cache;


public class CacheControllerException extends Exception{

	private String message;
	
	private static final long serialVersionUID = 1L;
	
	public CacheControllerException(String message){
		
		this.message = message;
		
	}
	
	@Override
	public String toString() {
		return message;
	}

}