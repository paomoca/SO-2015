
public class InodeNotEnoughDiskSpaceExcepcion extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public InodeNotEnoughDiskSpaceExcepcion(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return this.message;
	}
}
