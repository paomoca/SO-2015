package so.filesystem.disk;
public class DeviceInitializationException extends Exception {
	
	private String mensaje;
	
	private static final long serialVersionUID = 1L;
	
	public DeviceInitializationException(String mensaje){
		
		this.mensaje = mensaje;
		
	}
	
	@Override
	public String toString() {
		return mensaje;
	}

}
