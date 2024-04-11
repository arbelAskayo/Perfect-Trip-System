package entity;

import java.sql.ResultSet;

import java.sql.SQLException;

public class Member {
	private Long memberNumber;
	private String firstName;
	private String lastName;
	private String email;
	public Member(Long memberNumber, String firstName, String lastName, String email) {
		super();
		this.memberNumber = memberNumber;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}
	public Member(String email) {
		super();		
		this.email = email;
	}
	// Constructor that takes a ResultSet and creates a Member object
    public Member(ResultSet rs) throws SQLException {
        this.memberNumber = rs.getLong("memberID");
        this.firstName = rs.getString("firstName");
        this.lastName = rs.getString("lastName");
        this.email = rs.getString("email");
        
    }
	public Long getMemberNumber() {
		return memberNumber;
	}
	public void setMemberNumber(Long memberNumber) {
		this.memberNumber = memberNumber;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public String toString() {
		return firstName + " " + lastName;
	}
	
	

}
