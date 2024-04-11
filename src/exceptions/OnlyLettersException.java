package exceptions;

public class OnlyLettersException extends Exception{
	/*
	 * @author - Tomer Malkevich 316495126
	 */
	private static final long serialVersionUID = 1L;
	public OnlyLettersException() {
		super("Sorry, only letters here!");
	}
}
