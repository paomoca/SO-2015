package so.gui.shell;

public class RequestImportException extends Exception {
	private static final long serialVersionUID = 1L;
	private String message;

	public RequestImportException(String message) {

		this.message = message;

	}

	@Override
	public String toString() {
		return message;
	}
}
