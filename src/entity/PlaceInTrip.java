package entity;

import java.util.Date;

public class PlaceInTrip {
	private Integer tripId;
	private Integer placeId;
	private Date date;
	
	public PlaceInTrip(Integer tripId, Integer placeId, Date date) {
		super();
		this.tripId = tripId;
		this.placeId = placeId;
		this.date = date;
	}

	public Integer getTripId() {
		return tripId;
	}



	public void setTripId(Integer tripId) {
		this.tripId = tripId;
	}



	public Integer getPlaceId() {
		return placeId;
	}



	public void setPlaceId(Integer placeId) {
		this.placeId = placeId;
	}



	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
	
	

}
