package so.filesystem.disk;
public class DeviceInitializationException extends Exception {
	
	private String message;
	
	private static final long serialVersionUID = 1L;
	
	public DeviceInitializationException(String message){
		
		this.message = message;
		
	}
	
	@Override
	public String toString() {
		return message;
	}

}
