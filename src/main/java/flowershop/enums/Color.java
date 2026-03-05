package flowershop.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Color {
    WHITE("белый"),
    YELLOW("желтый"),
    PINK("розовый"),
    RED("красный"),
    GREEN("зеленый"),
    BLACK("черный");

    private final String russianName;

    Color(String russianName) {
        this.russianName = russianName;
    }

    @JsonValue
    public String getRussianName() {
        return russianName;
    }

    public static Color fromString(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        String trimmedValue = value.trim();

        for (Color color : Color.values()) {

            if (color.name().equalsIgnoreCase(trimmedValue) ||
                    color.getRussianName().equalsIgnoreCase(trimmedValue)) {
                return color;
            }
        }
        return null;
    }
}
