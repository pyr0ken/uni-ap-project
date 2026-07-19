package hotel.model;

public class Review {
    private final String reviewId;
    private final String roomNumber;
    private final String username;
    private final int rating;
    private final String comment;

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

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getUsername() {
        return username;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return reviewId + ";" + roomNumber + ";" + username + ";" + rating + ";" + comment.replace(";", ",");
    }
}
