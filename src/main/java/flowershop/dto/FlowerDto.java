package flowershop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Schema(description = "Объект передачи данных цветов")
public class FlowerDto {

    @Schema(description = "Уникальный идентификатор цветов", example = "1")
    private Long id;

    @Schema(description = "Название цветов", example = "Роза")
    @NotBlank(message = "Название не может быть пустым")
    @Size(min = 2, max = 30, message = "Название должно быть от 2 до 30 символов")
    private String name;

    @Schema(description = "Статус активности цветов", example = "true")
    @NotNull(message = "Укажите, активен ли цветок (true/false)")
    private boolean active;

    @Schema(description = "Цена цветов", example = "11")
    @NotNull(message = "Укажите цену")
    @Positive(message = "Цена должна быть больше нуля")
    private double price;

    @Schema(description = "Цвет", example = "красный")
    @NotNull(message = "Цвет не указан или указан некорректно. Доступные цвета: белый, желтый, розовый, красный, зеленый, черный")
    private String color;

}
