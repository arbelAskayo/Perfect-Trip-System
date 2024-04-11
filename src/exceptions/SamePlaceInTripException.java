package exceptions;

public class SamePlaceInTripException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public SamePlaceInTripException() {
		
		super("this place has been selected already within this trip");
	}

}
