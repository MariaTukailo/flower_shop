package flowershop.entity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Customer {
    private int id;
    private String name;
    private String phoneNumber;
    private ShoppingCart cart;
    private List<Order> orders;
}