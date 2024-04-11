package control;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import entity.Member;
import entity.Consts;

public class MemberControl {
    private static MemberControl instance;
    private HashMap<Long, Member> membersById; // Key: Member ID, Value: Member object
    
    public HashMap<Long, Member> getMembersById() {
		return membersById;
	}
	public void setMembersById(HashMap<Long, Member> membersById) {
		this.membersById = membersById;
	}
	// Singleton pattern to ensure only one instance of MemberControl is created
    public static MemberControl getInstance() {
        if (instance == null) {
            instance = new MemberControl();
            instance.loadMembersFromDB();
        }
        return instance;
    }
    private MemberControl() {
		super();
		this.membersById= new HashMap <Long, Member> ();
	}
    
    private void loadMembersFromDB() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // Load the JDBC driver and establish a connection
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            conn = DriverManager.getConnection(Consts.CONN_STR);

            // Prepare the SQL statement to fetch all members
            pstmt = conn.prepareStatement(Consts.SQL_SEL_MEMBERS);

            // Execute the query
            rs = pstmt.executeQuery();

            // Iterate over the results and populate the hashmap
            while (rs.next()) {
                long memberId = rs.getLong("memberID");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String email = rs.getString("email");

                // Create a new member object and put it in the hashmap
                Member member = new Member(memberId, firstName, lastName, email);
                this.getMembersById().put(memberId, member);
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
    }


   

    // Create a new member in the system and the database
    public Member createMember(Long memberID, String firstName, String lastName, String email) {
        // Check if memberID already exists to avoid duplicates
        if (this.getMembersById().containsKey(memberID)||this.getMemberByEmail(email)!=null) {
            // Handle the case where the member already exists, perhaps throw an exception or return null
            return null;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Load the JDBC driver and establish a connection
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            conn = DriverManager.getConnection(Consts.CONN_STR);

            // Prepare the SQL statement to insert the new member
            pstmt = conn.prepareStatement(Consts.SQL_INSERT_MEMBER);
            pstmt.setLong(1, memberID);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, email);

            // Execute the insert command
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating member failed, no rows affected.");
            }

            // Create the Member object and add it to the hashmap
            Member newMember = new Member(memberID, firstName, lastName, email);
            membersById.put(memberID, newMember);
            return newMember;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            // Handle the error properly - could be a duplicate key, etc.
        } finally {
            // Clean up and close JDBC resources
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        
        return null; // Return null if member creation failed
    }


    // Update existing member details
    public boolean updateMember(Member member) {
        if (member == null || !membersById.containsKey(member.getMemberNumber())) {
            // Member is null or does not exist in our current records
            return false;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Load the JDBC driver and establish a connection
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            conn = DriverManager.getConnection(Consts.CONN_STR);

            // Prepare the SQL statement to update the member
            pstmt = conn.prepareStatement(Consts.SQL_UPDATE_MEMBER);
            pstmt.setString(1, member.getFirstName());
            pstmt.setString(2, member.getLastName());
            pstmt.setString(3, member.getEmail());
            pstmt.setLong(4, member.getMemberNumber()); // Assuming Member ID cant be change and used to identify the member

            // Execute the update command
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Update successful, now update the hashmap
                membersById.put(member.getMemberNumber(), member);
                return true;
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // Clean up and close JDBC resources
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        
        return false; // Return false if the update failed
    }


    // Delete a member from the system and the database
    public boolean deleteMember(Member member) {
        if (member == null || !membersById.containsKey(member.getMemberNumber())) {
            // Member is null or does not exist in our current records
            return false;
        }
        //use getall reviews by member and update them to member calld deleted account ********************************
        //also to make sure to delete it from any relevant data base including in the controllers and the entities
        
        
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Load the JDBC driver and establish a connection
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            conn = DriverManager.getConnection(Consts.CONN_STR);

            // Prepare the SQL statement to delete the member
            pstmt = conn.prepareStatement(Consts.SQL_DELETE_MEMBER);
            pstmt.setLong(1, member.getMemberNumber());

            // Execute the delete command
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Delete successful, now remove the member from the hashmap
                membersById.remove(member.getMemberNumber());
                return true;
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // Clean up and close JDBC resources
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        
        return false; // Return false if the delete operation failed
    }


    // Retrieve a member by their ID
    public Member getMemberById(Long memberId) {
        // Check if the member ID is in the hashmap first
        if (membersById.containsKey(memberId)) {
            return membersById.get(memberId);
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Load the JDBC driver and establish a connection
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            conn = DriverManager.getConnection(Consts.CONN_STR);

            // Prepare the SQL statement to fetch the member by ID
            pstmt = conn.prepareStatement(Consts.SQL_SELECT_MEMBER_BY_ID);
            pstmt.setLong(1, memberId);

            // Execute the query
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Extract member details from the ResultSet
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String email = rs.getString("email");
                // Other member fields if they exist
                
                // Create and return a new Member object with the details retrieved
                Member member = new Member(memberId, firstName, lastName, email);
                membersById.put(memberId, member); // Cache the retrieved member if he is not in our hashMap
                return member;
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
        
        return null; // Member not found or an error occurred
    }
 // Method to retrieve member details by email
    public Member getMemberByEmail(String email) {
        // First, try to find the member in the hashmap
        for (Member member : membersById.values()) {
            if (member.getEmail().equalsIgnoreCase(email)) {
                return member; // Member found in hashmap
            }
        }
        
        // If not found in hashmap, query the database
        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement pstmt = conn.prepareStatement(Consts.SQL_GET_MEMBER_BY_EMAIL)) {
             
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Member member = new Member(rs);
                    membersById.put(member.getMemberNumber(), member);
                    return member;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception properly in your code
        }
        
        return null; // Member not found
    }


    // Retrieve all members
    public HashMap<Long,Member> getAllMembers() {
    	if (membersById.isEmpty()) {
            return  null;
        }
    	else {
    		return membersById;
    	}
		
    }



}
