package flowershop.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Объект передачи данных заказов")
public class OrderDto {

    @Schema(description = "Уникальный идентификатор", example = "1")
    private Long id;

    @Schema(description = "Дата заказа", example = "2026-05-20T14:30:00")
    private LocalDateTime date;

    @NotNull(message = "Дата доставки обязательна")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "Дата доставки не может быть в прошлом")
    @Schema(description = "Дата доставки заказа", example = "2026-05-20")
    private LocalDate deliveryDate;

    @NotNull(message = "Время доставки обязательно")
    @JsonFormat(pattern = "HH:mm")
    @Schema(description = "Время доставки заказа", example = "14:30:00")
    private LocalTime deliveryTime;

    @Schema(description = "Цена заказа в рублях", example = "55")
    private double finalPrice;

    @Schema(description = "Статус заказа", example = "принят")
    private String status;

    @Schema(description = "ID покупателя", example = "1")
    private Long customerId;

    @Schema(description = "Заказанные букеты")
    private List<BouquetDto> bouquets;
}
