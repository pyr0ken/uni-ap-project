package hotel.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Room {
    public enum RoomType {
        SINGLE("Single"),
        DOUBLE("Double"),
        SUITE("Suite");

        private final String displayName;

        RoomType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static RoomType fromString(String text) {
            for (RoomType type : RoomType.values()) {
                if (type.displayName.equalsIgnoreCase(text) || type.name().equalsIgnoreCase(text)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown room type: " + text);
        }
    }

    private String roomNumber;
    private RoomType type;
    private BigDecimal pricePerNight;
    private List<String> amenities;
    private int capacity;

    public Room(String roomNumber, RoomType type, BigDecimal pricePerNight, List<String> amenities, int capacity) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.amenities = new ArrayList<>(amenities);
        this.capacity = capacity;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getAmenitiesString() {
        return String.join(",", amenities);
    }

    @Override
    public String toString() {
        return roomNumber + ";" + type.getDisplayName() + ";" + pricePerNight + ";" + getAmenitiesString() + ";" + capacity;
    }
}
