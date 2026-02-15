package flower.shop.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Flower {

  private int catalogNumber;
  private String name;
  private double price;
  private String color;
  private String country;

}
