package flowershop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class FlowerDto {

    private Long id;
    private String name;
    private boolean active;
    private double price;
    private String color;

}
