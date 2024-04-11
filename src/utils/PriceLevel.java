package utils;

public enum PriceLevel {
	LOW,
	MEDIUM,
	HIGH;
	
	@Override
    public String toString() {
        // Customize the string representation if necessary
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
