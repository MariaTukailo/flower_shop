package flowershop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Объект передачи данных покупателя")
public class CustomerDto {

    @Schema(description = "Уникальный идентификатор", example = "1")
    private Long id;

    @Schema(description = "Имя покупателя", example = "Иван Иванов")
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 50, message = "Название должно быть от 2 до 50 символов")
    private String name;

    @Schema(description = "Номер телефона", example = "+375447973155")
    @NotBlank(message = "Номер телефона не может быть пустым")
    @Size(min = 12, max = 13, message = "Номер телефона состоит из 12 символов")
    private String phoneNumber;

    @Schema(description = "Id корзины", example = "1")
    private Long shoppingCartId;

    @Schema(description = "Id заказов")
    private List<Long> orderIds;
}
