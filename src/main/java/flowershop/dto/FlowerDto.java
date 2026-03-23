package flowershop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@Data
@NoArgsConstructor
@Schema(description = "Объект передачи данных цветов")
public class FlowerDto {

    @Schema(description = "Уникальный идентификатор цветов", example = "1")
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    @Schema(description = "Название цветов", example = "Роза")
    @Size(min = 2, max = 30, message = "Название должно быть от 2 до 30 символов")
    private String name;

    @NotNull(message = "Укажите, активен ли цветок (true/false)")
    @Schema(description = "Статус активности цветов", example = "true")
    private boolean active;

    @Positive(message = "Цена должна быть больше нуля")
    @Schema(description = "Цена цветов", example = "11")
    @NotNull(message = "Укажите цену")
    private double price;

    @Schema(description = "Цвет", example = "красный")
    @NotNull(message = "Цвет не указан или указан некорректно. Доступные цвета: белый, желтый, розовый, красный, зеленый, черный")
    private String color;

}
