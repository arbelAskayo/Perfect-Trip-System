package exceptions;

public class MissingInputException extends Exception{
	/*
	 * @author - Tomer Malkevich 316495126
	 */
	private static final long serialVersionUID = 1L;
	public MissingInputException() {
		super("this field is empty!");
	}
}
