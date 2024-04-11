package entity;

import java.util.Date;
import java.util.HashMap;

public class Trip {
private Long tripNumber;
private Date startDate;
private Date endDate;
private Long tripCreatorId;
private HashMap<Long,Place> tripPlaces ;
private HashMap<Long,Member> tripMembers ;
private boolean isConfirmed; // Field to keep track of confirmation status



//בשביל לעדכן את כל המקומות של הטיולים שעושים FETCH נצטרך לעשות שם לולאה
public Trip(Long tripNumber, Date startDate, Date endDate, Long tripCreatorId) {
	super();
	this.tripNumber = tripNumber;
	this.startDate = startDate;
	this.endDate = endDate;
	this.tripCreatorId = tripCreatorId;
	this.tripPlaces=new HashMap<>();
	this.tripMembers=new HashMap<>();

}
//Method to confirm the trip
public void confirm() {
    this.isConfirmed = true;
}
// Getter method for the confirmation status
public boolean isConfirmed() {
    return isConfirmed;
}
public void notConfirm() {
    this.isConfirmed = false;
}

public HashMap<Long, Member> getTripMembers() {
	return tripMembers;
}

public void setTripMembers(HashMap<Long, Member> tripMembers) {
	this.tripMembers = tripMembers;
}

public HashMap<Long, Place> getTripPlaces() {
	return tripPlaces;
}

public void setTripPlaces(HashMap<Long, Place> tripPlaces) {
	this.tripPlaces = tripPlaces;
}

public Long getTripCreatorId() {
	return tripCreatorId;
}

public void setTripCreatorId(Long tripCreatorId) {
	this.tripCreatorId = tripCreatorId;
}

public Long getTripNumber() {
	return tripNumber;
}
public void setTripNumber(Long tripNumber) {
	this.tripNumber = tripNumber;
}
public Date getStartDate() {
	return startDate;
}
public void setStartDate(Date startDate) {
	this.startDate = startDate;
}
public Date getEndDate() {
	return endDate;
}
public void setEndDate(Date endDate) {
	this.endDate = endDate;
}
@Override
public String toString() {
	return "Trip Number" + tripNumber;
}



}
