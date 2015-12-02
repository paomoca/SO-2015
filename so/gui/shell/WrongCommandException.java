package so.gui.shell;

public class WrongCommandException extends Exception {

	private static final long serialVersionUID = 1L;
	private String message;

	public WrongCommandException(String message) {

		this.message = message;

	}

	@Override
	public String toString() {
		return message;
	}
}
