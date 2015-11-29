

public class CacheControllerException extends Exception{

	private String mensaje;
	
	private static final long serialVersionUID = 1L;
	
	public CacheControllerException(String mensaje){
		
		this.mensaje = mensaje;
		
	}
	
	@Override
	public String toString() {
		return mensaje;
	}

}