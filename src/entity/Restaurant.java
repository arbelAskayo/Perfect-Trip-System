package entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


import utils.PriceLevel;

public class Restaurant extends Place {

	
	private HashSet<String> kitchenStyle;

	public Restaurant(Long placeId, String name, String description, PriceLevel priceLevel, String mapURL,
			String cityCode) {
		super(placeId, name, description, priceLevel, mapURL, cityCode);
		kitchenStyle= new HashSet<String>();
	}

	public HashSet<String> getKitchenStyle() {
		return kitchenStyle;
	}
	

	public void setKitchenStyle(HashSet<String> kitchenStyle) {
		this.kitchenStyle = kitchenStyle;
	}

	@Override
	public String toString() {
		return "Restaurant " + super.toString();
	}
	
	public String getResturantDetails() {
		return super.toString() + "\nRestaurant [kitchenStyle=" + kitchenStyle + "]";
	}
	
	
}
