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

    private final String reservationId;
    private final String username;
    private final String roomNumber;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;
    private final int totalGuests;
    private final BigDecimal totalCost;
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

    public String getUsername() {
        return username;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public int getTotalGuests() {
        return totalGuests;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
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
