package flowershop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class FlowerDto {

    private int catalogNumber;
    private String name;
    private double price;
    private String color;

}
