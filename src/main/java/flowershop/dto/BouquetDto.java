package flowershop.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class BouquetDto {

    private Long id;
    private String name;
    private boolean active;
    private double price;
    private boolean wrappingPaper;
    private boolean ribbon;
    private int countFlowers;

    private List<FlowerDto> flowers;
}
