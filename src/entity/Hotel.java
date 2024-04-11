package entity;

import java.util.List;
import utils.*;

public class Hotel extends Place {
	
	private Integer starRating;
	private List<AccomodationStyles> accomodationStylesList;
	
	public Hotel(Long placeId, String name, String description, PriceLevel priceLevel, String mapURL,
			String cityCode, Integer starRating, List<AccomodationStyles> accomodationStylesList) {
		super(placeId, name, description, priceLevel, mapURL, cityCode);
		this.starRating = starRating;
		this.accomodationStylesList = accomodationStylesList;
	}

	public Integer getStarRating() {
		return starRating;
	}

	public void setStarRating(Integer starRating) {
		this.starRating = starRating;
	}

	public List<AccomodationStyles> getAccomodationStylesList() {
		return accomodationStylesList;
	}

	public void setAccomodationStylesList(List<AccomodationStyles> accomodationStylesList) {
		this.accomodationStylesList = accomodationStylesList;
	}

	@Override
	public String toString() {
		return "Hotel " + super.toString();
	}
	
	public String getHotelDetails() {
		return super.toString() + "\nHotel [starRating=" + starRating + ", accomodationStylesList=" + accomodationStylesList + "]";
	}

	
}
