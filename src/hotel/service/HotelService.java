package hotel.service;

import hotel.db.SqliteDatabase;
import hotel.exception.HotelException;
import hotel.exception.InsufficientCreditException;
import hotel.exception.InvalidBookingDatesException;
import hotel.exception.RoomUnavailableException;
import hotel.model.Reservation;
import hotel.model.Reservation.ReservationStatus;
import hotel.model.Review;
import hotel.model.Room;
import hotel.model.User;
import hotel.util.PasswordUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class HotelService {
    private final SqliteDatabase db;
    private final List<User> users;
    private final List<Room> rooms;
    private final List<Reservation> reservations;
    private final List<Review> reviews;

    public HotelService() {
        this.db = new SqliteDatabase();
        this.users = db.loadUsers();
        this.rooms = db.loadRooms();
        this.reservations = db.loadReservations();
        this.reviews = db.loadReviews();
        
        // Run auto-completion of past bookings on startup
        autoCompleteReservations();
    }

    // --- Authentication ---
    public synchronized boolean registerUser(String username, String password, String firstName, String lastName) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return false;
        }
        username = username.trim();
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return false;
            }
        }
        String hashedPassword = PasswordUtil.hashPassword(password);
        User newUser = new User(username, hashedPassword, firstName, lastName, new BigDecimal("1000.00"));
        users.add(newUser);
        db.saveUsers(users);
        return true;
    }

    public synchronized User loginUser(String username, String password) {
        if (username == null || password == null) return null;
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username.trim()) && PasswordUtil.verifyPassword(password, u.getPassword())) {
                return u;
            }
        }
        return null;
    }

    public synchronized void addCredit(String username, BigDecimal amount) {
        if (amount.signum() <= 0) return;
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                u.addCredit(amount);
                db.saveUsers(users);
                break;
            }
        }
    }

    // --- Room Queries & Filtering ---
    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut, Room.RoomType type,
                                        BigDecimal minPrice, BigDecimal maxPrice, List<String> selectedAmenities,
                                        Integer guestCount) {
        return rooms.stream().filter(room -> {
            if (guestCount != null && room.getCapacity() < guestCount) {
                return false;
            }
            if (type != null && room.getType() != type) {
                return false;
            }
            if (minPrice != null && room.getPricePerNight().compareTo(minPrice) < 0) {
                return false;
            }
            if (maxPrice != null && room.getPricePerNight().compareTo(maxPrice) > 0) {
                return false;
            }
            if (selectedAmenities != null && !selectedAmenities.isEmpty()) {
                if (!room.getAmenities().containsAll(selectedAmenities)) {
                    return false;
                }
            }
            if (checkIn != null && checkOut != null) {
                return isRoomAvailable(room.getRoomNumber(), checkIn, checkOut);
            }
            return true;
        }).collect(Collectors.toList());
    }

    public boolean isRoomAvailable(String roomNumber, LocalDate checkIn, LocalDate checkOut) {
        for (Reservation res : reservations) {
            if (res.getRoomNumber().equals(roomNumber) && res.getStatus() == ReservationStatus.ACTIVE) {
                // Standard overlap condition: checkIn < res.checkOut AND checkOut > res.checkIn
                if (checkIn.isBefore(res.getCheckOutDate()) && checkOut.isAfter(res.getCheckInDate())) {
                    return false;
                }
            }
        }
        return true;
    }

    // --- Price & Discount Calculation ---
    public double calculateDiscountRate(LocalDate checkIn, LocalDate checkOut) {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) return 0.0;

        double discount = 0.0;
        // Long-term stay discount (more than 7 nights) -> 15%
        if (nights > 7) {
            discount += 0.15;
        }
        // Early booking discount (more than 14 days in advance) -> 10%
        long daysInAdvance = ChronoUnit.DAYS.between(LocalDate.now(), checkIn);
        if (daysInAdvance > 14) {
            discount += 0.10;
        }
        return Math.min(discount, 0.50); // Cap discount at 50%
    }

    public BigDecimal calculateTotalCost(Room room, LocalDate checkIn, LocalDate checkOut) {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) nights = 1;
        BigDecimal baseCost = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
        double discountRate = calculateDiscountRate(checkIn, checkOut);
        BigDecimal discount = BigDecimal.valueOf(discountRate);
        return baseCost.multiply(BigDecimal.ONE.subtract(discount)).setScale(2, RoundingMode.HALF_UP);
    }

    // --- Bookings & Reservation Actions ---
    public synchronized List<Reservation> makeReservations(String username, List<String> roomNumbers,
                                                           LocalDate checkIn, LocalDate checkOut, int guestCount)
            throws InvalidBookingDatesException, RoomUnavailableException, InsufficientCreditException {
        
        // 1. Validation checks
        if (checkIn == null || checkOut == null) {
            throw new InvalidBookingDatesException("Check-in and Check-out dates must be selected.");
        }
        if (!checkIn.isAfter(LocalDate.now().minusDays(1))) {
            throw new InvalidBookingDatesException("Check-in date cannot be in the past.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new InvalidBookingDatesException("Check-out date must be after the Check-in date.");
        }
        if (roomNumbers == null || roomNumbers.isEmpty()) {
            throw new RoomUnavailableException("No rooms selected for booking.");
        }

        // Find User
        User user = null;
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                user = u;
                break;
            }
        }
        if (user == null) {
            throw new InvalidBookingDatesException("Authenticated user not found.");
        }

        // 2. Room availability check and cost calculation
        BigDecimal totalBookingCost = BigDecimal.ZERO;
        List<Room> selectedRooms = new ArrayList<>();
        
        for (String roomNum : roomNumbers) {
            Room r = rooms.stream().filter(room -> room.getRoomNumber().equals(roomNum)).findFirst().orElse(null);
            if (r == null) {
                throw new RoomUnavailableException("Room " + roomNum + " does not exist.");
            }
            if (!isRoomAvailable(roomNum, checkIn, checkOut)) {
                throw new RoomUnavailableException("Room " + roomNum + " is already booked for the selected timeframe.");
            }
            selectedRooms.add(r);
            totalBookingCost = totalBookingCost.add(calculateTotalCost(r, checkIn, checkOut));
        }

        // 3. Credit check
        if (user.getCredit().compareTo(totalBookingCost) < 0) {
            throw new InsufficientCreditException(String.format("Insufficient credit. Total Cost: $%s, Your Credit: $%s",
                    totalBookingCost.toPlainString(), user.getCredit().toPlainString()));
        }

        // 4. Save and process reservations
        user.deductCredit(totalBookingCost);
        db.saveUsers(users);

        List<Reservation> createdReservations = new ArrayList<>();
        for (Room room : selectedRooms) {
            String reservationId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            BigDecimal roomCost = calculateTotalCost(room, checkIn, checkOut);
            
            // Assign guests proportionally or max allowed for the room
            int roomGuests = Math.min(guestCount, room.getCapacity());
            guestCount = Math.max(0, guestCount - roomGuests);
            // If there are still guests and this is the last room, add the remaining guests to this room
            if (roomNumIsLast(room.getRoomNumber(), selectedRooms) && guestCount > 0) {
                roomGuests += guestCount;
            }

            Reservation res = new Reservation(
                    reservationId,
                    username,
                    room.getRoomNumber(),
                    checkIn,
                    checkOut,
                    roomGuests == 0 ? room.getCapacity() : roomGuests, // Default to capacity if guests is 0
                    roomCost,
                    ReservationStatus.ACTIVE
            );
            
            reservations.add(res);
            createdReservations.add(res);
            
            // Generate Invoice file
            generateInvoice(res, user, room);
        }
        
        db.saveReservations(reservations);
        return createdReservations;
    }

    private boolean roomNumIsLast(String roomNum, List<Room> selectedRooms) {
        if (selectedRooms.isEmpty()) return false;
        return selectedRooms.get(selectedRooms.size() - 1).getRoomNumber().equals(roomNum);
    }

    // --- Invoice Generation ---
    private void generateInvoice(Reservation res, User user, Room room) {
        try {
            Files.createDirectories(Paths.get("invoices"));
            String invoicePath = "invoices/invoice_" + res.getReservationId() + ".txt";
            
            long nights = ChronoUnit.DAYS.between(res.getCheckInDate(), res.getCheckOutDate());
            double discountRate = calculateDiscountRate(res.getCheckInDate(), res.getCheckOutDate());
            BigDecimal originalCost = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(invoicePath))) {
                writer.write("====================================================\n");
                writer.write("           GRAND LUXE HOTEL & RESIDENCE             \n");
                writer.write("                 BOOKING INVOICE                    \n");
                writer.write("====================================================\n");
                writer.write(" Invoice ID     : " + res.getReservationId() + "\n");
                writer.write(" Date Issued    : " + LocalDate.now() + "\n");
                writer.write(" Guest Name     : " + user.getFirstName() + " " + user.getLastName() + "\n");
                writer.write(" Username       : " + user.getUsername() + "\n");
                writer.write("----------------------------------------------------\n");
                writer.write(" Room Number    : " + room.getRoomNumber() + "\n");
                writer.write(" Room Type      : " + room.getType().getDisplayName() + "\n");
                writer.write(" Capacity       : " + room.getCapacity() + " Guests\n");
                writer.write(" Price / Night  : $" + room.getPricePerNight().toPlainString() + "\n");
                writer.write("----------------------------------------------------\n");
                writer.write(" Check-in Date  : " + res.getCheckInDate() + "\n");
                writer.write(" Check-out Date : " + res.getCheckOutDate() + "\n");
                writer.write(" Duration       : " + nights + " Night(s)\n");
                writer.write(" Number of Guests: " + res.getTotalGuests() + "\n");
                writer.write("----------------------------------------------------\n");
                writer.write(" Base Cost      : $" + originalCost.toPlainString() + "\n");
                if (discountRate > 0) {
                    writer.write(" Applied Discount: " + (int)(discountRate * 100) + "%\n");
                    BigDecimal savedAmount = originalCost.multiply(BigDecimal.valueOf(discountRate)).setScale(2, RoundingMode.HALF_UP);
                    writer.write(" Total Saved    : -$" + savedAmount.toPlainString() + "\n");
                }
                writer.write("----------------------------------------------------\n");
                writer.write(" TOTAL PAID     : $" + res.getTotalCost().toPlainString() + "\n");
                writer.write(" Payment Method : Account Credit (Deducted)\n");
                writer.write(" Status         : PAID / ACTIVE\n");
                writer.write("====================================================\n");
                writer.write(" Thank you for choosing Grand Luxe Hotel. Enjoy your stay!\n");
                writer.write("====================================================\n");
            }
        } catch (IOException e) {
            System.err.println("Error generating invoice for " + res.getReservationId() + ": " + e.getMessage());
        }
    }

    // --- Cancellation ---
    public synchronized BigDecimal cancelReservation(String reservationId) throws HotelException {
        Reservation res = reservations.stream()
                .filter(r -> r.getReservationId().equals(reservationId))
                .findFirst().orElse(null);

        if (res == null) {
            throw new HotelException("Reservation not found.");
        }
        if (res.getStatus() != ReservationStatus.ACTIVE) {
            throw new HotelException("Reservation is already " + res.getStatus().getDisplayName().toLowerCase() + ".");
        }

        User user = users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(res.getUsername()))
                .findFirst().orElse(null);
        if (user == null) {
            throw new HotelException("User associated with booking not found.");
        }

        long daysToCheckIn = ChronoUnit.DAYS.between(LocalDate.now(), res.getCheckInDate());
        BigDecimal refundAmount;
        if (daysToCheckIn >= 2) {
            refundAmount = res.getTotalCost();
        } else {
            refundAmount = res.getTotalCost().multiply(new BigDecimal("0.50")).setScale(2, RoundingMode.HALF_UP);
        }

        res.setStatus(ReservationStatus.CANCELLED);
        user.addCredit(refundAmount);

        db.saveReservations(reservations);
        db.saveUsers(users);

        updateInvoiceStatus(reservationId, refundAmount);

        return refundAmount;
    }

    private void updateInvoiceStatus(String reservationId, BigDecimal refundAmount) {
        String invoicePath = "invoices/invoice_" + reservationId + ".txt";
        if (Files.exists(Paths.get(invoicePath))) {
            try {
                List<String> lines = Files.readAllLines(Paths.get(invoicePath));
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).contains("Status         :")) {
                        lines.set(i, " Status         : CANCELLED (Refunded $" + refundAmount.toPlainString() + ")");
                    }
                }
                Files.write(Paths.get(invoicePath), lines);
            } catch (IOException e) {
                System.err.println("Error updating invoice status for cancellation: " + e.getMessage());
            }
        }
    }

    // --- Auto-Complete Past Bookings ---
    public synchronized void autoCompleteReservations() {
        boolean modified = false;
        LocalDate today = LocalDate.now();
        for (Reservation res : reservations) {
            if (res.getStatus() == ReservationStatus.ACTIVE) {
                // If today is equal to or after check-out date, it's completed
                if (!today.isBefore(res.getCheckOutDate())) {
                    res.setStatus(ReservationStatus.COMPLETED);
                    modified = true;
                    // Update invoice status too
                    updateInvoiceToCompleted(res.getReservationId());
                }
            }
        }
        if (modified) {
            db.saveReservations(reservations);
        }
    }

    private void updateInvoiceToCompleted(String reservationId) {
        String invoicePath = "invoices/invoice_" + reservationId + ".txt";
        if (Files.exists(Paths.get(invoicePath))) {
            try {
                List<String> lines = Files.readAllLines(Paths.get(invoicePath));
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).contains("Status         :")) {
                        lines.set(i, " Status         : COMPLETED");
                    }
                }
                Files.write(Paths.get(invoicePath), lines);
            } catch (IOException e) {
                System.err.println("Error updating invoice status to completed: " + e.getMessage());
            }
        }
    }

    // --- Active Bookings Reminders ---
    public List<Reservation> getCheckInReminders(String username) {
        LocalDate today = LocalDate.now();
        return reservations.stream().filter(res -> 
                res.getUsername().equalsIgnoreCase(username) &&
                res.getStatus() == ReservationStatus.ACTIVE &&
                (res.getCheckInDate().equals(today) || res.getCheckInDate().equals(today.plusDays(1)))
        ).collect(Collectors.toList());
    }

    // --- Reviews & Ratings ---
    public synchronized Review addReview(String roomNumber, String username, int rating, String comment) throws HotelException {
        if (rating < 1 || rating > 5) {
            throw new HotelException("Rating must be between 1 and 5.");
        }
        // Verify user had a Completed reservation for this room
        boolean hadStay = reservations.stream().anyMatch(res ->
                res.getUsername().equalsIgnoreCase(username) &&
                res.getRoomNumber().equals(roomNumber) &&
                res.getStatus() == ReservationStatus.COMPLETED
        );

        if (!hadStay) {
            throw new HotelException("You can only review rooms where you have completed a stay.");
        }

        // Generate unique review ID
        String reviewId = "REV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Review review = new Review(reviewId, roomNumber, username, rating, comment);
        
        reviews.add(review);
        db.saveReviews(reviews);
        return review;
    }

    public List<Review> getRoomReviews(String roomNumber) {
        return reviews.stream()
                .filter(rev -> rev.getRoomNumber().equals(roomNumber))
                .collect(Collectors.toList());
    }

    public double getRoomAverageRating(String roomNumber) {
        List<Review> roomReviews = getRoomReviews(roomNumber);
        if (roomReviews.isEmpty()) return 0.0;
        double sum = 0;
        for (Review r : roomReviews) {
            sum += r.getRating();
        }
        return sum / roomReviews.size();
    }

    public List<Reservation> getUserReservations(String username) {
        return reservations.stream()
                .filter(res -> res.getUsername().equalsIgnoreCase(username))
                .collect(Collectors.toList());
    }
}
