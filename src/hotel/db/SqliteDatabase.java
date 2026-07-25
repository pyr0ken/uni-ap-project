package hotel.db;

import hotel.model.Reservation;
import hotel.model.Reservation.ReservationStatus;
import hotel.model.Review;
import hotel.model.Room;
import hotel.model.Room.RoomType;
import hotel.model.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SqliteDatabase {
    private static final String DATA_DIR = "data";
    private static final String DB_FILE = DATA_DIR + "/hotel.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found: " + e.getMessage());
        }
    }

    public SqliteDatabase() {
        initDatabase();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initDatabase() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create data directory: " + e.getMessage());
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "username TEXT PRIMARY KEY, " +
                    "password TEXT NOT NULL, " +
                    "first_name TEXT NOT NULL, " +
                    "last_name TEXT NOT NULL, " +
                    "credit REAL NOT NULL" +
                    ")");

            // Create Rooms table
            stmt.execute("CREATE TABLE IF NOT EXISTS rooms (" +
                    "room_number TEXT PRIMARY KEY, " +
                    "type TEXT NOT NULL, " +
                    "price_per_night REAL NOT NULL, " +
                    "amenities TEXT NOT NULL, " +
                    "capacity INTEGER NOT NULL" +
                    ")");

            // Create Reservations table
            stmt.execute("CREATE TABLE IF NOT EXISTS reservations (" +
                    "reservation_id TEXT PRIMARY KEY, " +
                    "username TEXT NOT NULL, " +
                    "room_number TEXT NOT NULL, " +
                    "check_in_date TEXT NOT NULL, " +
                    "check_out_date TEXT NOT NULL, " +
                    "total_guests INTEGER NOT NULL, " +
                    "total_cost REAL NOT NULL, " +
                    "status TEXT NOT NULL, " +
                    "FOREIGN KEY(username) REFERENCES users(username), " +
                    "FOREIGN KEY(room_number) REFERENCES rooms(room_number)" +
                    ")");

            // Create Reviews table
            stmt.execute("CREATE TABLE IF NOT EXISTS reviews (" +
                    "review_id TEXT PRIMARY KEY, " +
                    "room_number TEXT NOT NULL, " +
                    "username TEXT NOT NULL, " +
                    "rating INTEGER NOT NULL, " +
                    "comment TEXT NOT NULL, " +
                    "FOREIGN KEY(room_number) REFERENCES rooms(room_number), " +
                    "FOREIGN KEY(username) REFERENCES users(username)" +
                    ")");

            // Populate default rooms if table is empty
            if (isTableEmpty("rooms")) {
                createDefaultRooms();
            }

        } catch (SQLException e) {
            System.err.println("Failed to initialize SQLite database: " + e.getMessage());
        }
    }

    private static final java.util.Set<String> VALID_TABLES = java.util.Set.of(
        "users", "rooms", "reservations", "reviews"
    );

    private boolean isTableEmpty(String tableName) {
        if (!VALID_TABLES.contains(tableName)) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if table " + tableName + " is empty: " + e.getMessage());
        }
        return true;
    }

    private void createDefaultRooms() {
        List<Room> defaults = new ArrayList<>();
        defaults.add(new Room("101", RoomType.SINGLE, new BigDecimal("100.00"), Arrays.asList("TV", "Refrigerator", "Internet"), 1));
        defaults.add(new Room("102", RoomType.SINGLE, new BigDecimal("120.00"), Arrays.asList("TV", "Internet"), 1));
        defaults.add(new Room("103", RoomType.DOUBLE, new BigDecimal("180.00"), Arrays.asList("TV", "Refrigerator", "Internet"), 2));
        defaults.add(new Room("201", RoomType.DOUBLE, new BigDecimal("220.00"), Arrays.asList("TV", "Refrigerator", "Internet", "MiniBar"), 2));
        defaults.add(new Room("202", RoomType.SUITE, new BigDecimal("380.00"), Arrays.asList("TV", "Refrigerator", "Internet", "Kitchen", "Jacuzzi"), 4));
        defaults.add(new Room("203", RoomType.SUITE, new BigDecimal("450.00"), Arrays.asList("TV", "Refrigerator", "Internet", "Kitchen", "Jacuzzi", "Balcony"), 5));
        
        saveRooms(defaults);
    }

    // --- Users DB operations ---
    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT username, password, first_name, last_name, credit FROM users";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        new BigDecimal(rs.getString("credit"))
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading users from SQLite: " + e.getMessage());
        }
        return users;
    }

    public void saveUser(User user) {
        String sql = "INSERT OR REPLACE INTO users (username, password, first_name, last_name, credit) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getLastName());
            pstmt.setString(5, user.getCredit().toPlainString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving user to SQLite: " + e.getMessage());
        }
    }

    public void saveUsers(List<User> users) {
        String sql = "INSERT OR REPLACE INTO users (username, password, first_name, last_name, credit) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (User user : users) {
                    pstmt.setString(1, user.getUsername());
                    pstmt.setString(2, user.getPassword());
                    pstmt.setString(3, user.getFirstName());
                    pstmt.setString(4, user.getLastName());
                    pstmt.setString(5, user.getCredit().toPlainString());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error saving users to SQLite: " + e.getMessage());
        }
    }

    public void updateUserPassword(String username, String newHash) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newHash);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating user password: " + e.getMessage());
        }
    }

    // --- Rooms DB operations ---
    public List<Room> loadRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT room_number, type, price_per_night, amenities, capacity FROM rooms";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String roomNum = rs.getString("room_number");
                RoomType type = RoomType.fromString(rs.getString("type"));
                BigDecimal price = new BigDecimal(rs.getString("price_per_night"));
                List<String> amenities = new ArrayList<>();
                String amenitiesStr = rs.getString("amenities");
                if (amenitiesStr != null && !amenitiesStr.trim().isEmpty()) {
                    amenities.addAll(Arrays.asList(amenitiesStr.split(",")));
                }
                int capacity = rs.getInt("capacity");
                rooms.add(new Room(roomNum, type, price, amenities, capacity));
            }
        } catch (SQLException e) {
            System.err.println("Error loading rooms from SQLite: " + e.getMessage());
        }
        return rooms;
    }

    public void saveRoom(Room room) {
        String sql = "INSERT OR REPLACE INTO rooms (room_number, type, price_per_night, amenities, capacity) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, room.getRoomNumber());
            pstmt.setString(2, room.getType().getDisplayName());
            pstmt.setString(3, room.getPricePerNight().toPlainString());
            pstmt.setString(4, room.getAmenitiesString());
            pstmt.setInt(5, room.getCapacity());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving room to SQLite: " + e.getMessage());
        }
    }

    public void saveRooms(List<Room> rooms) {
        String sql = "INSERT OR REPLACE INTO rooms (room_number, type, price_per_night, amenities, capacity) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (Room room : rooms) {
                    pstmt.setString(1, room.getRoomNumber());
                    pstmt.setString(2, room.getType().getDisplayName());
                    pstmt.setString(3, room.getPricePerNight().toPlainString());
                    pstmt.setString(4, room.getAmenitiesString());
                    pstmt.setInt(5, room.getCapacity());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error saving rooms to SQLite: " + e.getMessage());
        }
    }

    // --- Reservations DB operations ---
    public List<Reservation> loadReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT reservation_id, username, room_number, check_in_date, check_out_date, total_guests, total_cost, status FROM reservations";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String id = rs.getString("reservation_id");
                String username = rs.getString("username");
                String roomNum = rs.getString("room_number");
                LocalDate checkIn = LocalDate.parse(rs.getString("check_in_date"));
                LocalDate checkOut = LocalDate.parse(rs.getString("check_out_date"));
                int guests = rs.getInt("total_guests");
                BigDecimal cost = new BigDecimal(rs.getString("total_cost"));
                ReservationStatus status = ReservationStatus.fromString(rs.getString("status"));
                reservations.add(new Reservation(id, username, roomNum, checkIn, checkOut, guests, cost, status));
            }
        } catch (SQLException e) {
            System.err.println("Error loading reservations from SQLite: " + e.getMessage());
        }
        return reservations;
    }

    public void saveReservation(Reservation res) {
        String sql = "INSERT OR REPLACE INTO reservations (reservation_id, username, room_number, check_in_date, check_out_date, total_guests, total_cost, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, res.getReservationId());
            pstmt.setString(2, res.getUsername());
            pstmt.setString(3, res.getRoomNumber());
            pstmt.setString(4, res.getCheckInDate().toString());
            pstmt.setString(5, res.getCheckOutDate().toString());
            pstmt.setInt(6, res.getTotalGuests());
            pstmt.setString(7, res.getTotalCost().toPlainString());
            pstmt.setString(8, res.getStatus().getDisplayName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving reservation to SQLite: " + e.getMessage());
        }
    }

    public void saveReservations(List<Reservation> reservations) {
        String sql = "INSERT OR REPLACE INTO reservations (reservation_id, username, room_number, check_in_date, check_out_date, total_guests, total_cost, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (Reservation res : reservations) {
                    pstmt.setString(1, res.getReservationId());
                    pstmt.setString(2, res.getUsername());
                    pstmt.setString(3, res.getRoomNumber());
                    pstmt.setString(4, res.getCheckInDate().toString());
                    pstmt.setString(5, res.getCheckOutDate().toString());
                    pstmt.setInt(6, res.getTotalGuests());
                    pstmt.setString(7, res.getTotalCost().toPlainString());
                    pstmt.setString(8, res.getStatus().getDisplayName());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error saving reservations to SQLite: " + e.getMessage());
        }
    }

    // --- Reviews DB operations ---
    public List<Review> loadReviews() {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT review_id, room_number, username, rating, comment FROM reviews";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String id = rs.getString("review_id");
                String roomNum = rs.getString("room_number");
                String username = rs.getString("username");
                int rating = rs.getInt("rating");
                String comment = rs.getString("comment");
                reviews.add(new Review(id, roomNum, username, rating, comment));
            }
        } catch (SQLException e) {
            System.err.println("Error loading reviews from SQLite: " + e.getMessage());
        }
        return reviews;
    }

    public void saveReview(Review rev) {
        String sql = "INSERT OR REPLACE INTO reviews (review_id, room_number, username, rating, comment) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rev.getReviewId());
            pstmt.setString(2, rev.getRoomNumber());
            pstmt.setString(3, rev.getUsername());
            pstmt.setInt(4, rev.getRating());
            pstmt.setString(5, rev.getComment());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving review to SQLite: " + e.getMessage());
        }
    }

    public void saveReviews(List<Review> reviews) {
        String sql = "INSERT OR REPLACE INTO reviews (review_id, room_number, username, rating, comment) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (Review rev : reviews) {
                    pstmt.setString(1, rev.getReviewId());
                    pstmt.setString(2, rev.getRoomNumber());
                    pstmt.setString(3, rev.getUsername());
                    pstmt.setInt(4, rev.getRating());
                    pstmt.setString(5, rev.getComment());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error saving reviews to SQLite: " + e.getMessage());
        }
    }
}
