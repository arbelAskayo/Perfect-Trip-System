package control;

import java.io.File;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumFmt;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import utils.AccomodationStyles;
import utils.PriceLevel;
import entity.*;

public class SearchControl {
	private static SearchControl instance;
	private HashMap<Long,Place> allPlaces ;
	private HashMap<Long,Restaurant> allRestaurants;
	private HashMap<Long,Hotel> allHotels;
	private HashMap<String, City> allCities;
	private HashSet<String> allKitchenStyles;
	
	public static SearchControl getInstance() {
		if (instance == null)
			instance = new SearchControl();
		return instance;
	}
	
	private SearchControl() {
		super();
		this.allPlaces = getPlaces();
		this.allHotels= getHotels();
		this.allRestaurants = getRestaurants();
		this.allCities = getCities();
		this.allKitchenStyles = getKitchenStyles();

	}

	public HashMap<Long, Restaurant> getAllRestaurants() {
		return allRestaurants;
	}

	public void setAllRestaurants(HashMap<Long, Restaurant> allRestaurants) {
		this.allRestaurants = allRestaurants;
	}

	public HashMap<Long, Hotel> getAllHotels() {
		return allHotels ;
	}

	public void setAllHotels(HashMap<Long, Hotel> allHotels) {
		this.allHotels = allHotels;
	}

	public static void setInstance(SearchControl instance) {
		SearchControl.instance = instance;
	}

	public static SearchControl get_instance() {
		return instance;
	}

	public static void set_instance(SearchControl instance) {
		SearchControl.instance = instance;
	}

	public HashMap<Long,Place> getAllPlaces() {
		return allPlaces;
	}

	public void setAllPlaces(HashMap<Long,Place> allPlaces) {
		this.allPlaces = allPlaces;
	}

	public HashMap<String, City> getAllCities() {
		return allCities;
	}

	public void setAllCities(HashMap<String, City> allCities) {
		this.allCities = allCities;
	}

	public HashSet<String> getAllKitchenStyles() {
		return allKitchenStyles;
	}

	public void setAllKitchenStyles(HashSet<String> allKitchenStyles) {
		this.allKitchenStyles = allKitchenStyles;
	}

	/*----------------------------------------- All Fetches -----------------------------------------*/
	/**
	 * fetches all places from DB file.
	 * @return HashMap of Places.
	 */
	public HashMap<Long,Place> getPlaces() {
		HashMap<Long,Place> results = new HashMap<Long,Place>();
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
					PreparedStatement stmt = conn.prepareStatement(Consts.SQL_SEL_PLACES);
					ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int i = 1;
					PriceLevel p;
					if( rs.getString(4).equals("LOW")) {
						p= PriceLevel.LOW;
					}
					else if( rs.getString(4).equals("MEDIUM")) {
						p= PriceLevel.MEDIUM;				
										}
					else {
						p= PriceLevel.HIGH;
					}
					Place place = new Place(rs.getLong(i++), rs.getString(i++),rs.getString(i++) , p, rs.getString(5),
							rs.getString(6));
					results.put(place.getPlaceId(),place);
					
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return results;
	}
	public HashMap<Long, Hotel> getHotels() {
	    HashMap<Long, Hotel> results = new HashMap<>();
	    try {
	        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
	        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	             PreparedStatement stmt = conn.prepareStatement(Consts.SQL_SEL_HOTELS);
	             ResultSet rs = stmt.executeQuery()) {

	            while (rs.next()) {
	                // Get the basic Place details and the star rating for the Hotel
	                Place p = this.allPlaces.getOrDefault(rs.getLong(1), null);
	                int starRating = rs.getInt(2);

	                // Now get the AccommodationStyles for this hotel
	                List<AccomodationStyles> accommodationStylesList = new ArrayList<>();
	                try (PreparedStatement accomStmt = conn.prepareStatement("SELECT AccommodationStyle FROM HotelAccommodation WHERE HotelID = ?")) {
	                    accomStmt.setLong(1, p.getPlaceId());
	                    try (ResultSet accomRs = accomStmt.executeQuery()) {
	                        while (accomRs.next()) {
	                            String accomStyleInitials = accomRs.getString("AccommodationStyle");
	                            try {
	                                accommodationStylesList.add(AccomodationStyles.valueOf(accomStyleInitials));
	                            } catch (IllegalArgumentException e) {
	                                // Handle case where AccomodationStyleInitials does not match enum (log it, throw exception, etc.)
	                            }
	                        }
	                    }
	                }

	                // Create the Hotel object
	                Hotel h = new Hotel(p.getPlaceId(), p.getName(), p.getDescription(), p.getPriceLevel(), p.getMapURL(), p.getCityCode(), starRating, accommodationStylesList);
	                results.put(h.getPlaceId(), h);
	                // Update the allPlaces HashMap in SearchControl
	                this.allPlaces.put(h.getPlaceId(), h);
	            }
	        }
	    } catch (SQLException | ClassNotFoundException e) {
	        e.printStackTrace();
	    }

	    return results;
	}



	
	/**
	 * fetches all Restaurants from DB file and updates the allPlaces hashMap.
	 * @return HashMap of Resturants.
	 */
	public HashMap<Long,Restaurant> getRestaurants() {
		HashMap<Long,Restaurant> results = new HashMap<Long,Restaurant>();
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
					PreparedStatement stmt = conn.prepareStatement(Consts.SQL_SEL_Restaurant);
					ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					
					//if the restaurant is already exist we just add the kitchenStyle to the list
					if(results.containsKey(rs.getLong(1))){
						results.get(rs.getLong(1)).getKitchenStyle().add(rs.getString(2));
						this.allPlaces.put(results.get(rs.getLong(1)).getPlaceId(), results.get(rs.getLong(1)));   
					}
					else {
					//if the restaurant is not already exist we create a restaurant object and add it to the places and to results
						
					Place p =this.allPlaces.getOrDefault(rs.getLong(1), null);
					this.allPlaces.remove(p);
					Restaurant r = new Restaurant(p.getPlaceId(),p.getName(),p.getDescription(),p.getPriceLevel(),p.getMapURL(),p.getCityCode());
					r.getKitchenStyle().add(rs.getString(2));
					results.put(r.getPlaceId(),r);
					this.allPlaces.put(r.getPlaceId(),r);
					}
					
				}
			} 
			catch(NullPointerException e) {
				e.printStackTrace();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return results;
	}
	
	/**
	 * fetches all cities from DB file.
	 * @return HashMap of cities.
	 */
	public HashMap<String,City> getCities() {
		HashMap<String,City> results = new HashMap<String,City>();
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
					PreparedStatement stmt = conn.prepareStatement(Consts.SQL_SEL_CITIES);
					ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int i = 1;
					City city = new City(rs.getString(i++),rs.getString(i++), rs.getString(i));
					results.put(city.getCityCode(),city);
					
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return results;
	}
	
	public HashSet<String> getKitchenStyles() {
	    HashSet<String> results = new HashSet<>();
	    String sql = "SELECT styleName FROM KitchenStyles";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {
	        
	        while (rs.next()) {
	            String styleName = rs.getString("styleName");
	            results.add(styleName);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return results;
	}

	/*----------------------------------------- Search by criteria -----------------------------------------*/
	public List<Place> searchByCriteria(String name, String city, PriceLevel priceLevel, AccomodationStyles accomodationStyle, Integer starRating, String kitchenStyle, String placeType) {
	    List<Place> results = new ArrayList<>();
	    

	    // Iterate over all places and apply the filters
	    for (Place place : allPlaces.values()) {
	        boolean matches = true;

	        // Check if the name matches
	        if (name != null && !name.trim().isEmpty()) {
	            matches &= place.getName().toLowerCase().contains(name.toLowerCase());
	        }

	        // Check if the city matches
	        if (matches && city != null && !city.trim().isEmpty()) {
	            matches &= place.getCityCode().equalsIgnoreCase(city); // Assuming you have city codes in place object
	        }

	        // Check if the price level matches
	        if (matches && priceLevel != null) {
	            matches &= place.getPriceLevel() == priceLevel;
	        }

	        // Check if the place type matches
	        if(placeType!=null) {
	        	if(placeType.equals("Restaurant")) {
	        	matches &= (place instanceof Restaurant && placeType.equals("Restaurant")); 
	        	}
	        	else if(placeType.equals("Hotel")) {
	        	matches &= (place instanceof Hotel && placeType.equals("Hotel")); 
	        	}
	        	else if(placeType.equals("Place")) {
	        		
	        	}
	        }
	        
	        // If the place is a Hotel, check for star rating and accommodation style
	        if (matches && place instanceof Hotel) {
	            Hotel hotel = (Hotel) place;

	            // Check if the star rating matches
	            if (starRating != null) {
	                matches &= hotel.getStarRating().equals(starRating);
	            }

	            // Check if the accommodation style matches
	            if (accomodationStyle != null) {
	                matches &= hotel.getAccomodationStylesList().contains(accomodationStyle);
	            }
	        }

	        // If the place is a Restaurant, check for kitchen style
	        if (matches && place instanceof Restaurant) {
	            Restaurant restaurant = (Restaurant) place;

	            // Check if the kitchen style matches
	            if (kitchenStyle != null && !kitchenStyle.trim().isEmpty()) {
	                matches &= restaurant.getKitchenStyle().contains(kitchenStyle);
	            }
	        }        	

	        // If all criteria match, add to results
	        if (matches) {
	            results.add(place);
	        }
	    }

	    return results;
	}
	/*----------------------------------------- Generate recommendation -----------------------------------------*/
	public List<Place> getGenerateRecommendation(Integer tripID, Double radius) {
	    List<Place> recommendedPlaces = new ArrayList<>();
	    if (tripID == null || radius == null) {
	        return recommendedPlaces; // Returning empty list if input parameters are not provided.
	    }

	    // The SQL query with place holders replaced by parameters.
	    String sql = Consts.SQL_GENERATE_RECOMMENDATION
	        .replace("[tripIDinput]", tripID.toString())
	        .replace("[MaxDist]", radius.toString());

	    try {
	        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
	        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	             PreparedStatement stmt = conn.prepareStatement(sql);
	             ResultSet rs = stmt.executeQuery()) {

	            while (rs.next()) {
	            	Place place = this.getAllPlaces().get(rs.getLong(1));
	                recommendedPlaces.add(place);
	            }
	        }
	    } catch (SQLException | ClassNotFoundException e) {
	        e.printStackTrace();
	    }

	    return recommendedPlaces;
	}

	
	/*----------------------------------------- Export report to word -----------------------------------------*/
	public void exportToWord(List<Place> recommendedPlaces, double radius) throws IOException {
	    XWPFDocument document = new XWPFDocument();
	    
	    // Title
	    XWPFParagraph titleParagraph = document.createParagraph();
	    titleParagraph.setAlignment(ParagraphAlignment.CENTER);
	    XWPFRun titleRun = titleParagraph.createRun();
	    titleRun.setText("Recommended Places within " + radius + " km");
	    titleRun.setBold(true);
	    titleRun.setFontSize(20);
	    titleRun.addBreak();
	    
	    // Create a table with headers
	    XWPFTable table = document.createTable();
	    
	    // Set Table Width to be Auto-sized
	    table.setWidth("100%");
	    
	    // Creating Header Row
	    XWPFTableRow headerRow = table.getRow(0); // First row
	    headerRow.getCell(0).setText("Place ID");
	    headerRow.addNewTableCell().setText("Name");
	    headerRow.addNewTableCell().setText("Description");
	    headerRow.addNewTableCell().setText("Price Level");
	    headerRow.addNewTableCell().setText("Map URL");
	    headerRow.addNewTableCell().setText("City Code");
	    
	    // Adding data for each place
	    for (Place place : recommendedPlaces) {
	        XWPFTableRow dataRow = table.createRow(); // Create new row for each place
	        dataRow.getCell(0).setText(place.getPlaceId().toString());
	        dataRow.getCell(1).setText(place.getName());
	        dataRow.getCell(2).setText(place.getDescription());
	        dataRow.getCell(3).setText(place.getPriceLevel().toString());
	        dataRow.getCell(4).setText(place.getMapURL());
	        dataRow.getCell(5).setText(place.getCityCode());
	    }
	    
	    // Write the Document in file system
	    try (FileOutputStream out = new FileOutputStream(new File("Recommendations.docx"))) {
	        document.write(out);
	    }
	    
	    JOptionPane.showMessageDialog(null, "Report generated successfully.");
	}

		


	/*------ ADD Place to trip (not functional , a base for future HW - there is no need in implementing it in this work) ------------*/

	/**
	 * Adding a new place to trip with the parameters received from the form. But there is no need in implement it in HW02
	 * return true if the insertion was successful, else - return false
     * @return 
	 */
	
	/*
	 * public boolean addPlaceToTrip(Long tripID, Long placeID, Date visitDate) {
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
					CallableStatement stmt = conn.prepareCall(Consts.SQL_INS_PLACE_TO_TRIP)) {
				
				int i = 1;
				stmt.setLong(i++, tripID); // can't be null
				stmt.setLong(i++, placeID); // can't be null
				stmt.setDate(i++, new java.sql.Date(visitDate.getTime()));// can't be null
				//execute
				stmt.executeUpdate();
				return true;
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	*/
}
	
