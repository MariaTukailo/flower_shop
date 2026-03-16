package flowershop.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
@Schema(description = "Объект передачи данных для букета")
public class BouquetDto {

    @Schema(description = "Уникальный идентификатор", example = "1")
    @NotNull()
    private Long id;

    @Schema(description = "Название букета", example = "Весенний")
    @NotBlank(message = "Название не может быть пустым")
    @Size(min = 2, max = 30, message = "Название должно быть от 2 до 30 символов")
    private String name;

    @Schema(description = "Статус доступности цветов", example = "true")
    @NotNull(message = "Укажите, активен ли букет (true/false)")
    private boolean active;

    @Schema(description = "Цена цветов в рублях", example = "12")
    @NotNull(message = "Укажите цену")
    @Positive(message = "Цена должна быть больше нуля")
    private double price;

    @Schema(description = "Наличие упаковочной бумаги", example = "true")
    @NotNull(message = "Укажите, используется ли упаковочная бумага в букете (true/false)")
    private boolean wrappingPaper;

    @Schema(description = "Наличие ленты", example = "true")
    @NotNull(message = "Укажите, используется  ли лента в букете (true/false)")
    private boolean ribbon;

    @Schema(description = "Количество цветов", example = "11")
    @NotNull(message = "Укажите количество цветов")
    @Positive(message = "Количество цветов должно быть больше нуля")
    private int countFlowers;

    @Valid
    @Schema(description = "Цветы, входящие в букет")
    @NotNull(message = "Букет обязательно должен содержать цветы")
    private List<FlowerDto> flowers;
}
