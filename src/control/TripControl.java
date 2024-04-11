package control;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.healthmarketscience.jackcess.expr.ParseException;

import entity.City;
import entity.Consts;
import entity.Hotel;
import entity.Member;
import entity.Place;
import entity.Restaurant;
import entity.Trip;
import utils.PriceLevel;

public class TripControl {
	private static TripControl instance;
	private HashMap<Long,Trip> allTrips;
	



	public HashMap<Long, Trip> getAllTrips() {
		return allTrips;
	}

	public void setAllTrips(HashMap<Long, Trip> allTrips) {
		this.allTrips = allTrips;
	}

	

	

	public static void setInstance(TripControl instance) {
		TripControl.instance = instance;
	}

	public static TripControl getInstance() {
		if (instance == null)
			instance = new TripControl();
		return instance;
	}
	
	private TripControl() {
		super();
		this.allTrips= getTrips();


	}
	//all fetches
	/**
	 * fetches all Trips from DB file.
	 * @return HashMap of Places.
	 */
	public HashMap<Long, Trip> getTrips() {
	    HashMap<Long, Trip> results = new HashMap<Long, Trip>();
	    try {
	        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
	        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	             PreparedStatement stmt = conn.prepareStatement(Consts.SQL_SEL_TRIPS);
	             ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                int i = 1;

	                Trip trip = new Trip(rs.getLong(i++), rs.getDate(i++), rs.getDate(i++), rs.getLong(i));
	                trip.setTripPlaces(getPlacesForTrip(trip.getTripNumber(), conn)); // Fetch places for this trip
	                trip.setTripMembers(getMembersForTrip(trip.getTripNumber(), conn)); // Fetch members for this trip
	                results.put(trip.getTripNumber(), trip);
	            }
	        }
	    } catch (ClassNotFoundException | SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return results;
	}
	private HashMap<Long, Place> getPlacesForTrip(Long tripId, Connection conn) {
	    HashMap<Long, Place> places = new HashMap<>();
	    try (PreparedStatement pstmt = conn.prepareStatement(Consts.SQL_SEL_PLACES_FOR_TRIP)) {
	        pstmt.setLong(1, tripId);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                Long placeId = rs.getLong("PlaceID");
	                String name = rs.getString("placeName");
	                String description = rs.getString("placeDescription");
	                PriceLevel priceLevel = PriceLevel.valueOf(rs.getString("priceLevel").toUpperCase()); 
	                String mapURL = rs.getString("mapURL");
	                String cityCode = rs.getString("cityCode");
	                // Assume we have a method in PlaceControl to create a place from ResultSet
	                Place place = new Place(placeId, name, description, priceLevel, mapURL, cityCode);
	                places.put(place.getPlaceId(), place);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return places;
	}

	private HashMap<Long, Member> getMembersForTrip(Long tripId, Connection conn) {
	    HashMap<Long, Member> members = new HashMap<>();
	    try (PreparedStatement pstmt = conn.prepareStatement(Consts.SQL_SEL_MEMBERS_FOR_TRIP)) {
	        pstmt.setLong(1, tripId);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                long memberId = rs.getLong("memberID"); 
	                String firstName = rs.getString("firstName"); 
	                String lastName = rs.getString("lastName"); 
	                String email = rs.getString("email"); 

	                // Create a new member object and put it in the hashmap
	                Member member = new Member(memberId, firstName, lastName, email);
	                members.put(member.getMemberNumber(), member);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return members;
	}
	//the methods:
	public Trip createTrip(Date startDate, Date endDate, Member creator) {
	    Connection conn = null;
	    PreparedStatement prepStmt = null;
	    ResultSet rsKey = null;

	    try {
	        
	        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
	        conn = DriverManager.getConnection(Consts.CONN_STR);

	        prepStmt = conn.prepareStatement(Consts.SQL_INS_TRIP, Statement.RETURN_GENERATED_KEYS);
	        prepStmt.setDate(1, new java.sql.Date(startDate.getTime()));
	        prepStmt.setDate(2, new java.sql.Date(endDate.getTime()));
	        prepStmt.setLong(3, creator.getMemberNumber());

	        int affectedRows = prepStmt.executeUpdate();
	        
	        if (affectedRows == 0) {
	            throw new SQLException("Creating trip failed, no rows affected.");
	        }

	        rsKey = prepStmt.getGeneratedKeys();
	        if (rsKey.next()) {
	            long newId = rsKey.getLong(1);
	            Trip newTrip = new Trip(newId, startDate, endDate, creator.getMemberNumber());
	            allTrips.put(newId, newTrip);
	            addTravelerToTrip(newTrip, creator);
	            return newTrip;
	        } else {
	            throw new SQLException("Creating trip failed, no ID obtained.");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (rsKey != null) {
	                rsKey.close();
	            }
	            if (prepStmt != null) {
	                prepStmt.close();
	            }
	            if (conn != null) {
	                conn.close();
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }

	    return null; // Return null if trip creation failed
	}



public boolean addPlaceToTrip(Trip trip, Place place, Date visitDate) {
    // Check if the trip and place exist in their respective HashMaps
    if (!allTrips.containsKey(trip.getTripNumber()) || !SearchControl.get_instance().getAllPlaces().containsKey(place.getPlaceId())) {
        return false; // Trip or Place does not exist
    }

    // Convert the visitDate from String to java.sql.Date
    java.sql.Date sqlVisitDate;
    sqlVisitDate = new java.sql.Date(visitDate.getTime());
    
    Connection conn = null;
    PreparedStatement prepStmt = null;

    try {
        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
        conn = DriverManager.getConnection(Consts.CONN_STR);
        prepStmt = conn.prepareStatement(Consts.SQL_ADD_PLACE_TO_TRIP);
        prepStmt.setLong(1, trip.getTripNumber());
        prepStmt.setLong(2, place.getPlaceId());
        prepStmt.setDate(3, sqlVisitDate);

        int affectedRows = prepStmt.executeUpdate();
        if (affectedRows > 0) {
            // Update the HashMap
            trip.getTripPlaces().put(place.getPlaceId(), place);
            allTrips.put(trip.getTripNumber(), trip);
            return true; // Place successfully added to trip
        }
    } catch (SQLException | ClassNotFoundException e) {
        e.printStackTrace();
    } finally {
        try {
            if (prepStmt != null) {
                prepStmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    return false; // Adding place to trip failed
}

	public boolean addTravelerToTrip(Trip trip, Member traveler) {
	    // Check if the trip and member exist in their respective HashMaps
	    if (!allTrips.containsKey(trip.getTripNumber()) || !MemberControl.getInstance().getAllMembers().containsKey(traveler.getMemberNumber())||(trip.getTripMembers().containsKey(traveler.getMemberNumber()))) {
	        return false; // Trip or Member does not exist + the member is already in the trip
	    }

	    Connection conn = null;
	    PreparedStatement prepStmt = null;

	    try {
	        // Load the JDBC driver and establish a connection
	        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
	        conn = DriverManager.getConnection(Consts.CONN_STR);

	        // Prepare the SQL statement for inserting a new traveler into the trip
	        prepStmt = conn.prepareStatement(Consts.SQL_ADD_TRAVELER_TO_TRIP);
	        prepStmt.setLong(2, trip.getTripNumber());
	        prepStmt.setLong(1, traveler.getMemberNumber());

	        // Execute the update and get the number of affected rows
	        int affectedRows = prepStmt.executeUpdate();

	        if (affectedRows > 0) {
	            // Update the 'allTrips' HashMap with the new member information
	            HashMap<Long, Member> tripMembers = trip.getTripMembers();
	            tripMembers.put(traveler.getMemberNumber(), traveler);

	            // Update the Trip object in the 'allTrips' HashMap
	            allTrips.put(trip.getTripNumber(), trip);

	            return true; // Indicate that the member was successfully added
	        }
	    } catch (SQLException | ClassNotFoundException e) {
	        e.printStackTrace();
	    } finally {
	        // Clean up and close JDBC resources
	        try {
	            if (prepStmt != null) {
	                prepStmt.close();
	            }
	            if (conn != null) {
	                conn.close();
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }

	    return false; // Indicate that adding the member to the trip failed
	}


	public boolean removePlaceFromTrip(Trip trip, Place place) {
	    // Check if the trip and place exist in their respective HashMaps
	    if (!allTrips.containsKey(trip.getTripNumber()) || !SearchControl.get_instance().getAllPlaces().containsKey(place.getPlaceId())) {
	        return false; // Trip or Place does not exist
	    }

	    Connection conn = null;
	    PreparedStatement prepStmt = null;

	    try {
	        // Load the JDBC driver and establish a connection
	        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
	        conn = DriverManager.getConnection(Consts.CONN_STR);

	        // Prepare the SQL statement for removing a place from the trip
	        prepStmt = conn.prepareStatement(Consts.SQL_REMOVE_PLACE_FROM_TRIP);
	        prepStmt.setLong(1, trip.getTripNumber());
	        prepStmt.setLong(2, place.getPlaceId());

	        // Execute the update and check if a row was affected
	        int affectedRows = prepStmt.executeUpdate();

	        if (affectedRows > 0) {
	            // Update the 'allTrips' HashMap by removing the place from the trip's places
	            HashMap<Long, Place> tripPlaces = trip.getTripPlaces();
	            tripPlaces.remove(place.getPlaceId());

	            // Update the Trip object in the 'allTrips' HashMap
	            allTrips.put(trip.getTripNumber(), trip);

	            return true; // Indicate that the place was successfully removed
	        }
	    } catch (SQLException | ClassNotFoundException e) {
	        e.printStackTrace();
	    } finally {
	        // Clean up and close JDBC resources
	        try {
	            if (prepStmt != null) {
	                prepStmt.close();
	            }
	            if (conn != null) {
	                conn.close();
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }

	    return false; // Indicate that removing the place from the trip failed
	}

	public boolean removeTravelerFromTrip(Trip trip, Member traveler) {
	    // Check if the trip and member exist in their respective HashMaps
	    if (!allTrips.containsKey(trip.getTripNumber()) || !MemberControl.getInstance().getAllMembers().containsKey(traveler.getMemberNumber())) {
	        return false; // Trip or Member does not exist
	    }

	    Connection conn = null;
	    PreparedStatement prepStmt = null;

	    try {
	        // Load the JDBC driver and establish a connection
	        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
	        conn = DriverManager.getConnection(Consts.CONN_STR);

	        // Prepare the SQL statement for removing a traveler from the trip
	        prepStmt = conn.prepareStatement(Consts.SQL_REMOVE_TRAVELER_FROM_TRIP);
	        prepStmt.setLong(1, trip.getTripNumber());
	        prepStmt.setLong(2, traveler.getMemberNumber());

	        // Execute the update and check if a row was affected
	        int affectedRows = prepStmt.executeUpdate();

	        if (affectedRows > 0) {
	            // Update the 'allTrips' HashMap by removing the traveler from the trip's members
	            trip.getTripMembers().remove(traveler.getMemberNumber());

	            // Update the Trip object in the 'allTrips' HashMap
	            allTrips.put(trip.getTripNumber(), trip);

	            return true; // Indicate that the traveler was successfully removed
	        }
	    } catch (SQLException | ClassNotFoundException e) {
	        e.printStackTrace();
	    } finally {
	        // Clean up and close JDBC resources
	        try {
	            if (prepStmt != null) {
	                prepStmt.close();
	            }
	            if (conn != null) {
	                conn.close();
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }

	    return false; // Indicate that removing the traveler from the trip failed
	}
// confirm the trip
	public boolean confirmTripConfiguration(Trip trip) {
	    // Check if the trip exists in the HashMap
	    if (!allTrips.containsKey(trip.getTripNumber())) {
	        return false; // Trip does not exist
	    }

	    // Confirm the trip in-memory
	    trip.confirm();

	    return true; // Indicate that the trip was successfully confirmed in-memory
	}

    public Trip getTripDetails(Long tripID) {
    	 if (allTrips.containsKey(tripID)) {
    	        return allTrips.get(tripID); // Return the trip details from the HashMap
    	    }
    	 //in case the trip does not exist we return null
    	 else {
    		 return null;
    	 }
    }
    public List<Trip> getAllTripsForMember(Member member) {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT t.* FROM Trips t JOIN MemberOfTrip mt ON t.tripID = mt.tripID WHERE mt.memberID = ?";
        
        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Set the member ID parameter on the query
            pstmt.setLong(1, member.getMemberNumber()); 
     
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    Trip trip = new Trip(
                        rs.getLong(i++), // tripId
                        rs.getDate(i++), // tripStartDate
                        rs.getDate(i++), // tripEndDate
                        rs.getLong(i)    // tripCreatorId
                    );
                    trips.add(trip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return trips;
    }

    public boolean cancelTrip(Trip trip) {
        // Check if the trip exists in the HashMap
        if (!allTrips.containsKey(trip.getTripNumber())) {
            return false; // Trip does not exist
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;

        try {
            // Load the JDBC driver and establish a connection
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            conn = DriverManager.getConnection(Consts.CONN_STR);

            // Begin transaction block
            conn.setAutoCommit(false);
            
            // Prepare the SQL statement to delete the trip from the database
            prepStmt = conn.prepareStatement(Consts.SQL_DELETE_TRIP);
            prepStmt.setLong(1, trip.getTripNumber());

            // Execute the deletion
            int affectedRows = prepStmt.executeUpdate();
            if (affectedRows > 0) {
                // Successfully deleted from the database; now remove from HashMap
                allTrips.remove(trip.getTripNumber());

                // Commit transaction
                conn.commit();

                return true; // Indicate that the trip was successfully deleted
            } else {
                // Rollback transaction
                conn.rollback();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback transaction in case of exceptions
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            // Clean up and close JDBC resources
            try {
                if (prepStmt != null) {
                    prepStmt.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto commit to true
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return false; // Indicate that deleting the trip failed
    }
    public boolean changeTripDates(Trip trip, Date newStartDate, Date newEndDate) {
        // Check if the trip exists in the allTrips HashMap
        if (!allTrips.containsKey(trip.getTripNumber())) {
            return false; // Trip does not exist
        }
        
        Connection conn = null;
        PreparedStatement prepStmt = null;

        try {
            // Load the JDBC driver and establish a connection
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            conn = DriverManager.getConnection(Consts.CONN_STR);

            // Prepare the SQL statement for updating the trip dates
            prepStmt = conn.prepareStatement(Consts.SQL_UPDATE_TRIP_DATES);
            prepStmt.setDate(1, new java.sql.Date(newStartDate.getTime()));
            prepStmt.setDate(2, new java.sql.Date(newEndDate.getTime()));
            prepStmt.setLong(3, trip.getTripNumber());

            // Execute the update and check if a row was affected
            int affectedRows = prepStmt.executeUpdate();
            if (affectedRows > 0) {
                // Update the trip in the allTrips HashMap
                trip.setStartDate(newStartDate);
                trip.setEndDate(newEndDate);
                allTrips.put(trip.getTripNumber(), trip);
                
                return true; // Indicate that the trip dates were successfully changed
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // Clean up and close JDBC resources
            try {
                if (prepStmt != null) {
                    prepStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return false; // Indicate that changing the trip dates failed
    }

    public boolean areDatesFreeForTrip(Trip trip, Date startDate, Date endDate) {
         if (trip == null || !allTrips.containsKey(trip.getTripNumber())) {
             return false; // Trip is null or does not exist in the record.
         }
  
         Connection conn = null;
         PreparedStatement pstmt = null;
         ResultSet rs = null;
  
         try {
             Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
             conn = DriverManager.getConnection(Consts.CONN_STR);
  
             String query = "SELECT COUNT(*) AS placeCount FROM placesInTrip "
                          + "WHERE tripID = ? AND "
                          + "(visitDate >= ? AND visitDate <= ?)";
  
             pstmt = conn.prepareStatement(query);
             pstmt.setLong(1, trip.getTripNumber());
             pstmt.setDate(2, new java.sql.Date(startDate.getTime()));
             pstmt.setDate(3, new java.sql.Date(endDate.getTime()));
  
             rs = pstmt.executeQuery();
  
             if (rs.next() && rs.getInt("placeCount") == trip.getTripPlaces().values().stream().count()) {
                 return true; // No places are visited between the given dates.
             }
  
         } catch (ClassNotFoundException | SQLException e) {
             e.printStackTrace();
         } finally {
             try {
                 if (rs != null) rs.close();
                 if (pstmt != null) pstmt.close();
                 if (conn != null) conn.close();
             } catch (SQLException ex) {
                 ex.printStackTrace();
             }
         }
  
         return false; // The dates are not free because one or more places are visited.
     }
//needs to know whether to implement it or not **************************************************************************************
   /* public boolean sendInvitationToFriends(Member member, List<String> friendEmails) {
		return false;
	}*/
}

