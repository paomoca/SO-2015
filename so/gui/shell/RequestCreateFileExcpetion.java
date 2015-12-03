package so.gui.shell;

public class RequestCreateFileExcpetion extends Exception {
	private static final long serialVersionUID = 1L;
	private String message;

	public RequestCreateFileExcpetion(String message) {

		this.message = message;

	}

	@Override
	public String toString() {
		return message;
	}
}
