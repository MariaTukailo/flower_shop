package flowershop.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Bouquet {

    private int catalogNumber;
    private String name;
    private boolean wrappingPaper;
    private boolean ribbon;

    private List <Flower> flowers;

}