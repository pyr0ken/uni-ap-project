package hotel.model;

public class Review {
    private String reviewId;
    private String roomNumber;
    private String username;
    private int rating; // 1 to 5
    private String comment;

    public Review(String reviewId, String roomNumber, String username, int rating, String comment) {
        this.reviewId = reviewId;
        this.roomNumber = roomNumber;
        this.username = username;
        this.rating = rating;
        this.comment = comment;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return reviewId + ";" + roomNumber + ";" + username + ";" + rating + ";" + comment.replace(";", ",");
    }
}
