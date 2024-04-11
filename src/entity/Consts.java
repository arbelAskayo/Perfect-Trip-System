package entity;
import java.io.File;
import java.net.URLDecoder;
public final class Consts {
  private Consts() {
	  throw new AssertionError();
  }
  
  protected static final String DB_FILEPATH= getDBPath();
  public static final String CONN_STR = "jdbc:ucanaccess://" + DB_FILEPATH + ";COLUMNORDER=DISPLAY";

	/*----------------------------------------- Place QUERIES -----------------------------------------*/
	public static final String SQL_SEL_PLACES = "SELECT * FROM Places";
	public static final String SQL_GENERATE_RECOMMENDATION = "SELECT * FROM (\n"
			+ "    SELECT Distances.placeID2 AS NearPlaceID, Places.placeName, Places.placeDescription, Places.cityCode, \n"
			+ "    (SELECT Avg(Review.score) FROM Reviews AS Review WHERE Review.placeID = Distances.placeID2) AS AvgScore\n"
			+ "    FROM placesInTrip, Distances, Places\n"
			+ "    WHERE (((Distances.placeID2) Not In (SELECT placeID FROM placesInTrip WHERE tripID = [tripIDinput])) \n"
			+ "    AND ((placesInTrip.tripID)=[tripIDinput]) \n"
			+ "    AND ((Distances.distance)<=[MaxDist]) \n"
			+ "    AND ((placesInTrip.placeID)=[Distances].[placeID1]) \n"
			+ "    AND ((Places.PlaceID)=[Distances].[placeID2]))\n"
			+ "\n"
			+ "    UNION\n"
			+ "\n"
			+ "    SELECT Distances.placeID1 AS NearPlaceID, Places.placeName, Places.placeDescription, Places.cityCode, \n"
			+ "    (SELECT Avg(Review.score) FROM Reviews AS Review WHERE Review.placeID = Distances.placeID1) AS AvgScore\n"
			+ "    FROM placesInTrip, Distances, Places\n"
			+ "    WHERE (((Distances.placeID1) Not In (SELECT placeID FROM placesInTrip WHERE tripID = [tripIDinput])) \n"
			+ "    AND ((placesInTrip.tripID)=[tripIDinput]) \n"
			+ "    AND ((Distances.distance)<=[MaxDist]) \n"
			+ "    AND ((placesInTrip.placeID)=[Distances].[placeID2]) \n"
			+ "    AND ((Places.PlaceID)=[Distances].[placeID1]))\n"
			+ ") AS CombinedQuery\n"
			+ "ORDER BY AvgScore DESC;\n"
			+ "";
	/*----------------------------------------- Restaurant QUERIES -----------------------------------------*/
	public static final String SQL_SEL_Restaurant = "SELECT * FROM ResturantKitchenStyle";
	
	/*----------------------------------------- KitchenStyles QUERIES -----------------------------------------*/

	public static final String SQL_INS_KITCHEN_STYLE = "INSERT INTO KitchenStyles (styleName) VALUES (?)";
    public static final String SQL_DEL_KITCHEN_STYLE = "DELETE FROM KitchenStyles WHERE styleName = ?";
    public static final String SQL_UPD_KITCHEN_STYLE = "UPDATE KitchenStyles SET styleName = ? WHERE styleName = ?";
    public static final String SQL_UPD_REST_KITCHEN_STYLE = "UPDATE ResturantKitchenStyle SET kitchenStyleName = ? WHERE kitchenStyleName = ?";
    public static final String SQL_SEL_KITCHEN_STYLES = "SELECT styleName FROM KitchenStyles";
    public static final String SQL_SEARCH_KITCHEN_STYLES = "SELECT styleName FROM KitchenStyles WHERE styleName LIKE ?";
    
	/*----------------------------------------- Hotels QUERIES -----------------------------------------*/
	public static final String SQL_SEL_HOTELS = "SELECT * FROM Hotels";
	/*----------------------------------------- Trips QUERIES -----------------------------------------*/
	public static final String SQL_SEL_TRIPS = "SELECT * FROM Trips";
	public static final String SQL_SEL_PLACES_FOR_TRIP = "SELECT p.* FROM PlacesInTrip pit JOIN Places p ON pit.placeId = p.placeId WHERE pit.tripId = ?";
	public static final String SQL_SEL_MEMBERS_FOR_TRIP = "SELECT m.* FROM MemberOfTrip mot JOIN Members m ON mot.memberId = m.memberId WHERE mot.tripId = ?";
	public static final String SQL_INS_TRIP ="INSERT INTO Trips (tripStartDate, tripEndDate, tripCreatorID) VALUES (?, ?, ?)";
	public static final String SQL_ADD_PLACE_TO_TRIP ="INSERT INTO placesInTrip (tripID, placeID, visitDate) VALUES (?, ?, ?)";
	public static final String SQL_ADD_TRAVELER_TO_TRIP ="INSERT INTO MemberOfTrip (memberID, tripID) VALUES (?, ?)";
	public static final String SQL_REMOVE_PLACE_FROM_TRIP = "DELETE FROM placesInTrip WHERE tripID = ? AND placeID = ?";
	public static final String SQL_REMOVE_TRAVELER_FROM_TRIP =  "DELETE FROM MemberOfTrip WHERE tripID = ? AND memberID = ?";
	public static final String SQL_DELETE_TRIP =  "DELETE FROM Trips WHERE tripID = ?";
	public static final String SQL_UPDATE_TRIP_DATES = 
		    "UPDATE Trips SET tripStartDate = ?, tripEndDate = ? WHERE tripID = ?";

	/*----------------------------------------- Cities QUERIES -----------------------------------------*/
	public static final String SQL_SEL_CITIES = "SELECT * FROM Cities";
	/*----------------------------------------- Member QUERIES -----------------------------------------*/
	public static final String SQL_SEL_MEMBERS = "SELECT * FROM Members";
	public static final String SQL_INSERT_MEMBER = 
	        "INSERT INTO Members (memberID, firstName, lastName, email) VALUES (?, ?, ?, ?)";
	public static final String SQL_UPDATE_MEMBER = 
	        "UPDATE Members SET firstName = ?, lastName = ?, email = ? WHERE memberID = ?";
	 public static final String SQL_DELETE_MEMBER = 
		        "DELETE FROM Members WHERE memberID = ?";
	 public static final String SQL_SELECT_MEMBER_BY_ID = 
	            "SELECT * FROM Members WHERE memberID = ?";
	 public static final String SQL_GET_MEMBER_BY_EMAIL = "SELECT * FROM Members WHERE email = ?";
	/*----------------------------------------- Review QUERIES -----------------------------------------*/
	public static final String SQL_ADD_REVIEW = 
	        "INSERT INTO Reviews (memberID, placeID, timeSubmitted, score, comment) VALUES (?, ?, ?, ?, ?)";
	public static final String SQL_CHECK_REVIEW_EXISTS = 
	        "SELECT 1 FROM Reviews WHERE reviewID = ?";
	public static final String SQL_UPDATE_REVIEW = 
	            "UPDATE Reviews SET comment = ?, score = ?, timeSubmitted = ? WHERE reviewID = ?";
	public static final String SQL_DELETE_REVIEW = 
	            "DELETE FROM Reviews WHERE reviewID = ?";
	public static final String SQL_SELECT_REVIEWS_BY_PLACE = 
	        "SELECT * FROM Reviews WHERE placeID = ?";
	public static final String SQL_SELECT_REVIEWS_BY_MEMBER = 
	        "SELECT * FROM Reviews WHERE memberID = ?";

  /**
	 * find the correct path of the DB file
   * @return the path of the DB file (from eclipse or with runnable file)
	 */
	private static String getDBPath() {
		try {
//			
			String path = Consts.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String decoded = URLDecoder.decode(path, "UTF-8");
			System.out.println(""+decoded);
			// System.out.println(decoded) - Can help to check the returned path
			if (decoded.contains("/bin")) {
				System.out.println("got into else");
				decoded = decoded.substring(0, decoded.lastIndexOf("bin/"));
				System.out.println(decoded);
				return decoded + "src/entity/Perfect_Trip_DataBase.accdb";					
			} else {
				return "src/entity/Perfect_Trip_DataBase.accdb";
			}
		} catch (Exception e) {
			System.out.println("got exception");
			e.printStackTrace();
			return null;
		}
	}

  
	

	
	
	
	
	
	
	
	
	
	
  
}
