package exceptions;

public class NegativeNumberException extends Exception{
	
	private static final long serialVersionUID = 1L;
	public NegativeNumberException() {
		super("The number in this field is not suppose to be negative!");
	}
 
}