package flowershop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Объект передачи данных корзин")
public class ShoppingCartsDto {

    @Schema(description = "ID покупателя", example = "1")
    private Long customerId;

    @Schema(description = "Букеты в корзине")
    private List<BouquetDto> bouquets;
}
