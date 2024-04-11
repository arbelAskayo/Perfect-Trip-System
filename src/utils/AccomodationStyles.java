package utils;

public enum AccomodationStyles {
	AI,
	BB,
	HB,
	FB,
	RO;
	
	@Override
    public String toString() {
        // Customize the string representation if necessary
        return name().charAt(0) + name().substring(1);
    }
}
