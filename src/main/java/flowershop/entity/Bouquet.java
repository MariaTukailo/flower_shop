package flowershop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
@Entity
@Table(name = "bouquets")
@NoArgsConstructor
public class Bouquet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean active;
    private String name;
    private double price;
    private String pathPhoto;
    private boolean wrappingPaper;
    private boolean ribbon;
    private int countFlowers;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "bouquet_flowers",
            joinColumns = @JoinColumn(name = "bouquet_id"),
            inverseJoinColumns = @JoinColumn(name = "flower_id")
    )

    private List<Flower> flowers = new ArrayList<>();

    @ManyToMany(mappedBy = "bouquets", fetch = FetchType.LAZY)
    private List<ShoppingCart> shoppingCarts = new ArrayList<>();

    @ManyToMany(mappedBy = "bouquets", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

}