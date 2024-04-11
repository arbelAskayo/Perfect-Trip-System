package entity;

import java.sql.Date;

import control.SearchControl;


public class Review {
    private Long reviewId;
    private Long memberId;
    private Long placeId;
    private String comment;
    private int score;
    private Date reviewDate; 
    // Constructor
    public Review(Long reviewId, Long memberId, Long placeId, String comment, int score, Date reviewDate) {
        this.reviewId = reviewId;
        this.memberId = memberId;
        this.placeId = placeId;
        this.comment = comment;
        this.score = score;
        this.reviewDate = reviewDate;
    }

	public Long getReviewId() {
		return reviewId;
	}

	public void setReviewId(Long reviewId) {
		this.reviewId = reviewId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Long getPlaceId() {
		return placeId;
	}

	public void setPlaceId(Long placeId) {
		this.placeId = placeId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}

	@Override
	public String toString() {
		return "Review for: " + SearchControl.getInstance().getAllPlaces().get(placeId).getName();
	}

    
}

