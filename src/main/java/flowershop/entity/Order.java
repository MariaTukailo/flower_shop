package flowershop.entity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Order {

    private Long id;
    private LocalDateTime date;
    private String status;
    private Customer customer;
    private List<Bouquet> bouquets;

}