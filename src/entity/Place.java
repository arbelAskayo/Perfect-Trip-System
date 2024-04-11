package entity;

import java.util.HashMap;

import utils.PriceLevel;

public class Place {
	//צריך להוסיף גם רשימה של REVIEWS 
	private Long placeId;
	private String name;
	private String description;
	private PriceLevel priceLevel;
	private String mapURL;
	private String cityCode;
	
	
	
	public Place(Long placeId, String name, String description, PriceLevel priceLevel, String mapURL,
			String cityCode) {
		super();
		this.placeId = placeId;
		this.name = name;
		this.description = description;
		this.priceLevel = priceLevel;
		this.mapURL = mapURL;
		this.cityCode = cityCode;
	}


	public Long getPlaceId() {
		return placeId;
	}

	public void setPlaceId(Long placeId) {
		this.placeId = placeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public PriceLevel getPriceLevel() {
		return priceLevel;
	}


	public void setPriceLevel(PriceLevel priceLevel) {
		this.priceLevel = priceLevel;
	}

	public String getMapURL() {
		return mapURL;
	}

	public void setMapURL(String mapURL) {
		this.mapURL = mapURL;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	
	@Override
	public String toString() {
		return  name;
	}

	public String getDetails() {
		
		return "Place [name=" + name + ", description=" + description + ", priceLevel=" + priceLevel + ", mapURL="
				+ mapURL + ", cityCode=" + cityCode + "]";	}


	
	
}
