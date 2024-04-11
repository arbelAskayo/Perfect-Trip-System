package control;

import entity.City;
import entity.Consts;
import entity.Hotel;
import entity.Place;
import entity.Restaurant;
import utils.AccomodationStyles;
import utils.PriceLevel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.io.File;
import javax.swing.JOptionPane;
import java.sql.*;

public class ContentControl {
    private static ContentControl instance;
    private Random random = new Random();

    // Singleton pattern to ensure only one instance of ContentControl is created
    public static synchronized ContentControl getInstance() {
        if (instance == null) {
            instance = new ContentControl();
        }
        return instance;
    }

    // Private constructor to enforce singleton property
    private ContentControl() {
        // Private constructor to prevent instantiation
    }

    // Method to load and import places from an XML file
    public void importPlacesFromXML(String xmlFilePath) {
        File xmlFile = new File(xmlFilePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("Place");
            for (int i = 0; i < nList.getLength(); i++) {
                Element element = (Element) nList.item(i);

                // Assuming these fields are present in the XML file and the Place entity
                String placeId = element.getElementsByTagName("PlaceID").item(0).getTextContent();
                String placeName = element.getElementsByTagName("placeName").item(0).getTextContent();
                String description = element.getElementsByTagName("placeDescription").item(0).getTextContent();
                String priceLevel = element.getElementsByTagName("priceLevel").item(0).getTextContent();
                String googleMapsURL = element.getElementsByTagName("mapURL").item(0).getTextContent();
                String cityCode = element.getElementsByTagName("cityCode").item(0).getTextContent();
                String country = element.getElementsByTagName("Country").item(0).getTextContent();
                String cityName = element.getElementsByTagName("CityName").item(0).getTextContent();
                String type = element.getElementsByTagName("Type").item(0).getTextContent();

                Place place;
                switch (type) {
                    case "Hotel":
                        int starRating = Integer.parseInt(element.getElementsByTagName("starRating").item(0).getTextContent());
                        // Parse accomodationStylesList from XML
                        List<AccomodationStyles> styles = parseAccomodationStyles(element);
 
                        place = new Hotel(Long.parseLong(placeId), placeName, description, PriceLevel.valueOf(priceLevel), googleMapsURL, cityCode, starRating, styles);
                        break;
                    case "Restaurant":
                        HashSet<String> kitchenStyles = parseKitchenStyles(element);
                        place = new Restaurant(Long.parseLong(placeId), placeName, description, PriceLevel.valueOf(priceLevel), googleMapsURL, cityCode);
                        ((Restaurant)place).setKitchenStyle(kitchenStyles);
                        break;
                    default:
                         place = new Place(Long.parseLong(placeId), placeName, description, PriceLevel.valueOf(priceLevel), googleMapsURL, cityCode);
                        break;
                }
                
                // Call the method to insert the place with the provided ID.
                long newOrOld = insertPlace(place,country,cityName);

                // Now generate and insert distances for this new place.
                if(newOrOld == 2) { // check if insert was successful
                    generateAndInsertDistancesForPlace(place.getPlaceId());
                }
             
            }
            JOptionPane.showMessageDialog(null, "Places imported successfully.", "Import Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error importing places: " + e.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private List<AccomodationStyles> parseAccomodationStyles(Element placeElement) {
        List<AccomodationStyles> styles = new ArrayList<>();
        String stylesStr = placeElement.getElementsByTagName("AccomodationStyles").item(0).getTextContent();
        String[] stylesArray = stylesStr.split(",");

        for (String styleStr : stylesArray) {
            String cleanedStyle = styleStr.trim().toLowerCase();
            switch (cleanedStyle) {
                case "all inclusive":
                    styles.add(AccomodationStyles.AI);
                    break;
                case "bed and breakfast":
                    styles.add(AccomodationStyles.BB);
                    break;
                case "full board":
                    styles.add(AccomodationStyles.FB);
                    break;
                case "half board":
                    styles.add(AccomodationStyles.HB);
                    break;
                case "room only":
                    styles.add(AccomodationStyles.RO);
                    break;
                default:
                    System.out.println(cleanedStyle + " is not a recognized AccomodationStyle");
                    break;
            }
        }

        return styles;
    }


    private HashSet<String> parseKitchenStyles(Element placeElement) {
        HashSet<String> kitchenStyles = new HashSet<>();
        String stylesStr = placeElement.getElementsByTagName("KitchenStyles").item(0).getTextContent();
        String[] stylesArray = stylesStr.split(",");
        Collections.addAll(kitchenStyles, stylesArray);
        return kitchenStyles;
    }
    // Method to generate and insert random distances for a new place
    private void generateAndInsertDistancesForPlace(long newPlaceId) throws SQLException {
        Connection conn = null;
        PreparedStatement insertStmt = null;
        
        try {
            conn = DriverManager.getConnection(Consts.CONN_STR);
            conn.setAutoCommit(false); // Start transaction

            // Query to fetch all place IDs
            String sqlFetchPlaceIds = "SELECT PlaceID FROM Places";
            PreparedStatement fetchStmt = conn.prepareStatement(sqlFetchPlaceIds);
            ResultSet rs = fetchStmt.executeQuery();
            
            // Prepare statement for inserting distances
            String sqlInsertDistance = "INSERT INTO Distances (placeID1, placeID2, distance) VALUES (?, ?, ?)";
            insertStmt = conn.prepareStatement(sqlInsertDistance);

            // Insert distance of 0 for the place itself
            insertStmt.setLong(1, newPlaceId);
            insertStmt.setLong(2, newPlaceId);
            insertStmt.setDouble(3, 0);
            insertStmt.executeUpdate(); // Execute immediately for the place itself

            // For each existing place, generate a distance and insert it into Distances table
            for (long existingPlaceId : SearchControl.get_instance().getAllPlaces().keySet()) {
                if (existingPlaceId != newPlaceId) {
                    double distance = generateRandomDistance();
                    
                    if (!distanceExists(conn, newPlaceId, existingPlaceId)) {
                        insertStmt.setLong(1, newPlaceId);
                        insertStmt.setLong(2, existingPlaceId);
                        insertStmt.setDouble(3, distance);
                        insertStmt.addBatch();
                    }
                    
                    if (!distanceExists(conn, existingPlaceId, newPlaceId)) {
                        insertStmt.setLong(1, existingPlaceId);
                        insertStmt.setLong(2, newPlaceId);
                        insertStmt.setDouble(3, distance);
                        insertStmt.addBatch();
                    }
                }
            }
            
            // Execute batch insert for all other distances
            insertStmt.executeBatch(); 
            conn.commit(); // Commit the transaction
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            // Clean up and close JDBC resources
            if (insertStmt != null) try { insertStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset to default auto-commit mode
                    conn.close();
                } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    private boolean distanceExists(Connection conn, long placeId1, long placeId2) throws SQLException {
        String checkDistanceSql = "SELECT COUNT(*) FROM Distances WHERE placeID1 = ? AND placeID2 = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkDistanceSql)) {
            checkStmt.setLong(1, placeId1);
            checkStmt.setLong(2, placeId2);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    @SuppressWarnings("resource")
    private long insertPlace(Place place, String country, String cityName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        long generatedId = -1;
        int newOrOld=0;
        
        // Get place details
        long placeId = place.getPlaceId();
        String placeName = place.getName();
        String description = place.getDescription();
        String priceLevel = place.getPriceLevel().name();
        String googleMapsURL = place.getMapURL();
        String cityCode = place.getCityCode();

        try {
            // Establish a connection
            conn = DriverManager.getConnection(Consts.CONN_STR);
            conn.setAutoCommit(false); // Start transaction

            // Check if the place already exists
            String checkSql = "SELECT COUNT(*) FROM [Places] WHERE [PlaceID] = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setLong(1, placeId);
            rs = pstmt.executeQuery();
            checkAndAddCityCountry(cityCode, cityName, country);

            if (rs.next() && rs.getInt(1) > 0) {
                // Place exists, update it
                String updateSql = "UPDATE [Places] SET [placeName] = ?, [placeDescription] = ?, [priceLevel] = ?, [cityCode] = ? WHERE [PlaceID] = ?";
                pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, placeName);
                pstmt.setString(2, description);
                pstmt.setString(3, priceLevel);
                pstmt.setString(4, cityCode);
                pstmt.setLong(5, placeId);
                pstmt.executeUpdate();
                newOrOld=1;
            } else {
            	 // Place does not exist, insert a new one
                String insertSql = "INSERT INTO [Places] ([PlaceID], [placeName], [placeDescription], [priceLevel], [mapURL], [cityCode]) VALUES (?, ?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(insertSql);
                pstmt.setLong(1, placeId);
                pstmt.setString(2, placeName);
                pstmt.setString(3, description);
                pstmt.setString(4, priceLevel);
                pstmt.setString(5, googleMapsURL);
                pstmt.setString(6, cityCode);
                pstmt.executeUpdate();
                newOrOld=2;
                
                // Check if it's a Restaurant or Hotel and insert into the respective table
                if (place instanceof Restaurant) {
                    insertRestaurant((Restaurant) place, conn);
                } else if (place instanceof Hotel) {
                    insertHotel((Hotel) place, conn);
                }
            }

            conn.commit(); // Commit the changes

            // Update the allPlaces HashMap in SearchControl
            if (place.getPlaceId() != -1) {
                SearchControl.getInstance().getAllPlaces().put(place.getPlaceId(), place);
                if (place instanceof Restaurant) {
                    SearchControl.getInstance().getAllRestaurants().put(place.getPlaceId(), (Restaurant) place);
                } else if (place instanceof Hotel) {
                    SearchControl.getInstance().getAllHotels().put(place.getPlaceId(), (Hotel) place);
                }
            }
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            ex.printStackTrace();
            return -1; // Indicate an error occurred
        } finally {
            // Clean up and close JDBC resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.setAutoCommit(true); // Reset to default auto-commit mode
                if (conn != null) conn.close();
            } catch (SQLException finalEx) {
                finalEx.printStackTrace();
            }
        }
        return newOrOld;
    }
    private void insertRestaurant(Restaurant restaurant, Connection conn) throws SQLException {
        // Insert the restaurant into the Restaurant table
        String insertRestaurantSql = "INSERT INTO Resturant (resturantID) VALUES (?)";
        try (PreparedStatement insertRestaurantStmt = conn.prepareStatement(insertRestaurantSql)) {
            insertRestaurantStmt.setLong(1, restaurant.getPlaceId());
            insertRestaurantStmt.executeUpdate();
        }

        // Insert each kitchen style into RestaurantKitchenStyle table
        String insertStyleSql = "INSERT INTO ResturantKitchenStyle (resturantID, kitchenStyleName) VALUES (?, ?)";
        try (PreparedStatement insertStyleStmt = conn.prepareStatement(insertStyleSql)) {
            for (String style : restaurant.getKitchenStyle()) {
                insertStyleStmt.setLong(1, restaurant.getPlaceId());
                insertStyleStmt.setString(2, style);
                insertStyleStmt.executeUpdate();
            }
        }
    }
    private void insertHotel(Hotel hotel, Connection conn) throws SQLException {
        // Insert the hotel into the Hotels table
        String insertHotelSql = "INSERT INTO Hotels (hotelID, starRating) VALUES (?, ?)";
        try (PreparedStatement insertHotelStmt = conn.prepareStatement(insertHotelSql)) {
            insertHotelStmt.setLong(1, hotel.getPlaceId());
            insertHotelStmt.setInt(2, hotel.getStarRating());
            insertHotelStmt.executeUpdate();
        }

        // Insert into HotelAccommodation
        String insertHotelAccommodationSql = "INSERT INTO HotelAccommodation (HotelID, AccommodationStyle) VALUES (?, ?)";
        try (PreparedStatement insertHotelAccStmt = conn.prepareStatement(insertHotelAccommodationSql)) {
            for (AccomodationStyles style : hotel.getAccomodationStylesList()) {
                insertHotelAccStmt.setLong(1, hotel.getPlaceId());
                insertHotelAccStmt.setString(2, getStyleInitials(style));
                insertHotelAccStmt.executeUpdate();
            }
        }
    }

    private String getStyleInitials(AccomodationStyles style) {
        // This method should return the initials as expected in the AccomodationStyles table
        switch (style) {
            case AI:
                return "AI";
            case BB:
                return "BB";
            case HB:
                return "HB";
            case FB:
                return "FB";
            case RO:
                return "RO";
            default:
                throw new IllegalArgumentException("Unknown AccomodationStyle: " + style);
        }
    }







    // Helper method to generate a random distance
    private double generateRandomDistance() {
        return 100 + (10000 - 100) * random.nextDouble(); 
    }
    public void checkAndAddCityCountry(String cityCode, String cityName, String cityCountry) {
        // Check if the country exists
        int countryCode = extractCountryCodeFromCityCode(cityCode); 
        if (!countryExists(cityCountry)) {
            addCountryToDB(cityCountry, countryCode); 
        }

        // Check if the city exists
        if (!cityExists(cityCode)) {
            addCityToDB(cityCode, cityName, cityCountry); 
        }
    }
//we need to get the country code from the XML
    private boolean countryExists(String countryName) {

        
        // Check in the database
        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM Country WHERE CountryName = ?")) {
            stmt.setString(1, countryName);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0; // Country exists if count is greater than 0
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    private boolean cityExists(String cityCode) {
        boolean existsInMemory = SearchControl.getInstance().getAllCities().containsKey(cityCode);

        if (existsInMemory) {
            return true; // The city is already loaded in the memory
        }

        // Check in the database
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(Consts.CONN_STR);
            String query = "SELECT COUNT(*) AS Count FROM Cities WHERE CityID = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, cityCode);
            rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("Count");
                return count > 0; // City exists if count is greater than 0
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up and close JDBC resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }


    private void addCountryToDB(String countryName, int countryCode) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DriverManager.getConnection(Consts.CONN_STR);
            String sql = "INSERT INTO Country (CountryName, CountryCode) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, countryName);
            stmt.setInt(2, countryCode);
            stmt.executeUpdate(); // Execute the insert operation
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Clean up and close JDBC resources
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void addCityToDB(String cityCode, String cityName, String countryName) {
    	// Check if country exists
        if (!countryExists(countryName)) {
        	addCountryToDB(countryName,extractCountryCodeFromCityCode(cityCode)); // Method to add country
        }
    	
        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Cities (CityID, cityName, cityCounty) VALUES (?, ?, ?)")) {
            stmt.setString(1, cityCode);
            stmt.setString(2, cityName);
            stmt.setString(3, countryName);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                //updating the search control
                SearchControl.getInstance().getCities().put(cityCode, new City(cityCode, cityName, countryName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String getCountryNameByCityCode(String cityCode) {
        int countryCode = extractCountryCodeFromCityCode(cityCode);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String countryName = null;

        try {
            conn = DriverManager.getConnection(Consts.CONN_STR);
            String sql = "SELECT CountryName FROM Country WHERE CountryCode = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, countryCode);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                countryName = rs.getString("CountryName");
            } else {
                throw new SQLException("No country found with code: " + countryCode);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle exception properly - possibly rethrow or return null
        } finally {
            // Clean up and close JDBC resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException finalEx) {
                finalEx.printStackTrace();
            }
        }

        return countryName;
    }
    private int extractCountryCodeFromCityCode(String cityCode) {
        StringBuilder countryCodeBuilder = new StringBuilder();
        for (char ch : cityCode.toCharArray()) {
            if (Character.isDigit(ch)) {
                countryCodeBuilder.append(ch);
            } else {
                // Once we encounter a letter, the country code part is over.
                break;
            }
        }
        // Parse the string builder's content into an integer.
        return Integer.parseInt(countryCodeBuilder.toString());
    }


    

}
