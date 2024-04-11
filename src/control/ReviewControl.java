package control;

import java.sql.Statement;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.io.FileWriter;
import java.util.List;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.sql.Timestamp;

import entity.Consts;
import entity.Member;
import entity.Place;
import entity.Review;

public class ReviewControl {
    // Singleton instance
    private static ReviewControl instance;

    // Private constructor to prevent instantiation
    private ReviewControl() {
    }

    // Public static method to get the instance
    public static ReviewControl getInstance() {
        if (instance == null) {
            instance = new ReviewControl();
        }
        return instance;
    }
	 public boolean addReview(Member member, Place place, String comment, int score) {
	        // Use try-with-resources for efficient management of JDBC resources
	        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	             PreparedStatement pstmt = conn.prepareStatement(Consts.SQL_ADD_REVIEW, Statement.RETURN_GENERATED_KEYS)) {

	            // Set parameters for the prepared statement based on the method arguments
	            pstmt.setLong(1, member.getMemberNumber());
	            pstmt.setLong(2, place.getPlaceId());
	            pstmt.setString(5, comment);
	            pstmt.setInt(4, score);
	            // add the current date as the review date
	            pstmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));

	            int affectedRows = pstmt.executeUpdate();
	            if (affectedRows == 0) {
	                throw new SQLException("Creating review failed, no rows affected.");
	            }

	            // Optionally, handle the generated keys if you need to do something with them
	            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    long reviewId = generatedKeys.getLong(1);
	                    // Here you could return true, or if you needed the Review ID for something,
	                    // you could modify the method to return it or the whole Review object
	                } else {
	                    throw new SQLException("Creating review failed, no ID obtained.");
	                }
	            }
	            return true; // Indicate success
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false; // Indicate failure
	        }
	    }
	 public boolean updateReview(Review review) {
		    boolean reviewExists = false;
		    
		    Connection conn = null;
		    PreparedStatement checkStmt = null;
		    PreparedStatement updateStmt = null;
		    ResultSet rs = null;

		    try {
		        // Load the JDBC driver and establish a connection
		        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
		        conn = DriverManager.getConnection(Consts.CONN_STR);

		        // First, check if the review exists in the database
		        checkStmt = conn.prepareStatement(Consts.SQL_CHECK_REVIEW_EXISTS);
		        checkStmt.setLong(1, review.getReviewId());

		        rs = checkStmt.executeQuery();
		        reviewExists = rs.next();

		        if (!reviewExists) {
		            return false; // The review does not exist, cannot update
		        }

		        // Since the review exists, we can proceed to update
		        updateStmt = conn.prepareStatement(Consts.SQL_UPDATE_REVIEW);
		        updateStmt.setString(1, review.getComment());
		        updateStmt.setInt(2, review.getScore());
		        updateStmt.setDate(3, new java.sql.Date(review.getReviewDate().getTime())); // Bind the review date
		        updateStmt.setLong(4, review.getReviewId()); //the last parameter

		        int affectedRows = updateStmt.executeUpdate();
		        return affectedRows > 0;

		    } catch (SQLException | ClassNotFoundException e) {
		        e.printStackTrace();
		    } finally {
		        // Clean up and close JDBC resources
		        try {
		            if (rs != null) rs.close();
		            if (checkStmt != null) checkStmt.close();
		            if (updateStmt != null) updateStmt.close();
		            if (conn != null) conn.close();
		        } catch (SQLException ex) {
		            ex.printStackTrace();
		        }
		    }
		    
		    return false;
		}

	 public boolean deleteReview(Review review) {
		    boolean reviewExists = false;

		    Connection conn = null;
		    PreparedStatement checkStmt = null;
		    PreparedStatement pstmt = null;
		    ResultSet rs = null;

		    try {
		        // Load the JDBC driver and establish a connection
		        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
		        conn = DriverManager.getConnection(Consts.CONN_STR);

		        // First, check if the review exists in the database
		        checkStmt = conn.prepareStatement(Consts.SQL_CHECK_REVIEW_EXISTS);
		        checkStmt.setLong(1, review.getReviewId());

		        rs = checkStmt.executeQuery();
		        reviewExists = rs.next();

		        // Close the resources used for checking
		        rs.close();
		        checkStmt.close();

		        if (!reviewExists) {
		            return false; // The review does not exist, cannot delete
		        }

		        // Prepare the SQL statement to delete the review
		        pstmt = conn.prepareStatement(Consts.SQL_DELETE_REVIEW);
		        pstmt.setLong(1, review.getReviewId());

		        // Execute the delete command
		        int affectedRows = pstmt.executeUpdate();
		        
		        return affectedRows > 0; // Return true if the review was successfully deleted
		    } catch (SQLException | ClassNotFoundException e) {
		        e.printStackTrace();
		    } finally {
		        // Clean up and close JDBC resources
		        try {
		            if (rs != null) rs.close();
		            if (checkStmt != null) checkStmt.close();
		            if (pstmt != null) pstmt.close();
		            if (conn != null) conn.close();
		        } catch (SQLException ex) {
		            ex.printStackTrace();
		        }
		    }
		    
		    return false; // Return false if the attempt to delete the review failed
		}


	 public List<Review> getReviewsByPlace(Place place) {
		    List<Review> reviews = new ArrayList<>();
		    
		    Connection conn = null;
		    PreparedStatement pstmt = null;
		    ResultSet rs = null;

		    try {
		        // Load the JDBC driver and establish a connection
		        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
		        conn = DriverManager.getConnection(Consts.CONN_STR);

		        // Prepare the SQL statement to fetch the reviews for the given place
		        pstmt = conn.prepareStatement(Consts.SQL_SELECT_REVIEWS_BY_PLACE);
		        pstmt.setLong(1, place.getPlaceId());

		        // Execute the query
		        rs = pstmt.executeQuery();

		        // Iterate over the results and build the list of Review objects
		        while (rs.next()) {
		            long reviewId = rs.getLong("reviewID");
		            long memberId = rs.getLong("memberID");
		            String comment = rs.getString("comment");
		            int score = rs.getInt("score");
		            Timestamp timestamp = rs.getTimestamp("timeSubmitted");
		            Date reviewDate = new Date(timestamp.getTime());

		            Review review = new Review(reviewId, memberId, place.getPlaceId(), comment, score, new java.sql.Date(reviewDate.getTime()));
		            reviews.add(review);
		        }

		        
		    } catch (SQLException | ClassNotFoundException e) {
		        e.printStackTrace();
		    } finally {
		        // Clean up and close JDBC resources
		        try {
		            if (rs != null) rs.close();
		            if (pstmt != null) pstmt.close();
		            if (conn != null) conn.close();
		        } catch (SQLException ex) {
		            ex.printStackTrace();
		        }
		    }

		    return reviews; // Return the list of reviews
		}

	 public List<Review> getReviewsByMember(Member member) {
		    List<Review> reviews = new ArrayList<>();

		    Connection conn = null;
		    PreparedStatement pstmt = null;
		    ResultSet rs = null;

		    try {
		        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
		        conn = DriverManager.getConnection(Consts.CONN_STR);

		        pstmt = conn.prepareStatement(Consts.SQL_SELECT_REVIEWS_BY_MEMBER);
		        pstmt.setLong(1, member.getMemberNumber());

		        rs = pstmt.executeQuery();

		        while (rs.next()) {
		            long reviewId = rs.getLong("reviewID");
		            long memberId = rs.getLong("memberID");
		            long placeId = rs.getLong("placeID");
		            String comment = rs.getString("comment");
		            int score = rs.getInt("score");
		            Date reviewDate = new Date(rs.getDate("timeSubmitted").getTime()) ;

		            Review review = new Review(reviewId, memberId, placeId, comment, score, new java.sql.Date(reviewDate.getTime()));
		            reviews.add(review);
		        }
		    } catch (SQLException | ClassNotFoundException e) {
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

		    return reviews;
		}
	 /**
	     * Fetches all reviews written in a given year.
	     * @param year The year to fetch reviews for.
	     * @return A list of Review objects.
	     */
	    public List<Review> getReviewsForYear(int year) {
	        List<Review> reviews = new ArrayList<>();
	        String query = "SELECT * FROM Reviews WHERE YEAR(timeSubmitted) = ?";

	        try {
	            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
	            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	                 PreparedStatement pstmt = conn.prepareStatement(query)) {
	                
	                pstmt.setInt(1, year);

	                try (ResultSet rs = pstmt.executeQuery()) {
	                    while (rs.next()) {
	                        long reviewId = rs.getLong("reviewID");
	                        long memberId = rs.getLong("memberID");
	                        long placeId = rs.getLong("placeID");
	                        String comment = rs.getString("comment");
	                        int score = rs.getInt("score");
	                        java.sql.Date reviewDate = rs.getDate("timeSubmitted");

	                        Review review = new Review(reviewId, memberId, placeId, comment, score, reviewDate);
	                        reviews.add(review);
	                    }
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return reviews;
	    }
	    public boolean exportReviewsToWord(int year) {
	        List<Review> reviews = getReviewsForYear(year); 
	        try (XWPFDocument document = new XWPFDocument()) {
	            XWPFParagraph title = document.createParagraph();
	            title.setAlignment(ParagraphAlignment.CENTER);
	            XWPFRun titleRun = title.createRun();
	            titleRun.setText("Reviews for the year " + year);
	            titleRun.setBold(true);
	            titleRun.setFontSize(20);

	            for (Review review : reviews) {
	                XWPFParagraph p = document.createParagraph();
	                XWPFRun run = p.createRun();
	                run.setText("Review ID: " + review.getReviewId());
	                run.addBreak();
	                run.setText("Member ID: " + review.getMemberId());
	                run.addBreak();
	                run.setText("Place ID: " + review.getPlaceId());
	                run.addBreak();
	                run.setText("Score: " + review.getScore());
	                run.addBreak();
	                run.setText("Comment: " + review.getComment());
	                run.addBreak();
	                run.setText("Date: " + review.getReviewDate().toString());
	                run.addBreak();
	                run.addBreak(); // Add an extra space before the next review
	            }

	            // write the document to an output stream
	            try (FileOutputStream out = new FileOutputStream("Reviews_" + year + ".docx")) {
	                document.write(out);
	                return true;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	

}
