package flowershop.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchKey {
    private String flowerName;
    private List<String> orderStatuses;
    LocalDate date;
    private int page;
    private int size;
}
