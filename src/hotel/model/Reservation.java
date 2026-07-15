package hotel.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Reservation {
    public enum ReservationStatus {
        ACTIVE("Active"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled");

        private final String displayName;

        ReservationStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static ReservationStatus fromString(String text) {
            for (ReservationStatus status : ReservationStatus.values()) {
                if (status.displayName.equalsIgnoreCase(text) || status.name().equalsIgnoreCase(text)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown reservation status: " + text);
        }
    }

    private String reservationId;
    private String username;
    private String roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int totalGuests;
    private BigDecimal totalCost;
    private ReservationStatus status;

    public Reservation(String reservationId, String username, String roomNumber, LocalDate checkInDate, LocalDate checkOutDate, int totalGuests, BigDecimal totalCost, ReservationStatus status) {
        this.reservationId = reservationId;
        this.username = username;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalGuests = totalGuests;
        this.totalCost = totalCost;
        this.status = status;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getTotalGuests() {
        return totalGuests;
    }

    public void setTotalGuests(int totalGuests) {
        this.totalGuests = totalGuests;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return reservationId + ";" + username + ";" + roomNumber + ";" + checkInDate + ";" + checkOutDate + ";" + totalGuests + ";" + totalCost + ";" + status.getDisplayName();
    }
}
