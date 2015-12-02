package so.gui.shell;

public class ShellAnswerException extends Exception {
	private static final long serialVersionUID = 1L;
	private String message;

	public ShellAnswerException(String message) {

		this.message = message;

	}

	@Override
	public String toString() {
		return message;
	}
}
