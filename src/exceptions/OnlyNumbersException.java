package exceptions;

public class OnlyNumbersException extends Exception {
	/*
	 * @author - Tomer Malkevich 316495126
	 */
	private static final long serialVersionUID = 1L;
	public OnlyNumbersException() {
		super("Sorry, only numbers here!");
	}
}
