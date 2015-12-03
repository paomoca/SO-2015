package so.gui.shell;

public class RequestExportException extends Exception {
	private static final long serialVersionUID = 1L;
	private String message;

	public RequestExportException(String message) {

		this.message = message;

	}

	@Override
	public String toString() {
		return message;
	}
}
