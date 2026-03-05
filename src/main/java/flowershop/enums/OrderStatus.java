package flowershop.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
    PROCESSING("Обработка"),
    ACCEPTED("Принят"),
    IN_TRANSIT("В пути"),
    DELIVERED("Доставлен");

    private final String russianName;

    OrderStatus(String russianName) {
        this.russianName = russianName;
    }

    @JsonValue
    public String getRussianName() {
        return russianName;
    }

    public static OrderStatus fromString(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        String trimmedValue = value.trim();

        for (OrderStatus status : OrderStatus.values()) {

            if (status.name().equalsIgnoreCase(trimmedValue) ||
                    status.getRussianName().equalsIgnoreCase(trimmedValue)) {
                return status;
            }
        }
        return null;
    }
}
