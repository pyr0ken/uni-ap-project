package hotel.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    private final String roomNumber;
    private final RoomType type;
    private final BigDecimal pricePerNight;
    private final List<String> amenities;
    private final int capacity;

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

    public RoomType getType() {
        return type;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public List<String> getAmenities() {
        return Collections.unmodifiableList(amenities);
    }

    public int getCapacity() {
        return capacity;
    }

    public String getAmenitiesString() {
        return String.join(",", amenities);
    }

    @Override
    public String toString() {
        return roomNumber + ";" + type.getDisplayName() + ";" + pricePerNight + ";" + getAmenitiesString() + ";" + capacity;
    }
}
