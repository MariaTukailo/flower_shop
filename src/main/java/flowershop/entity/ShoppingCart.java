package flowershop.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ShoppingCart {

    private int id;
    private List<Bouquet> bouquet;
}
