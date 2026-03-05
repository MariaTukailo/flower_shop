package flowershop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "shopping_carts")
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart {

    @Id
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @MapsId

    Customer customer;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "cart_bouquets",
            joinColumns = @JoinColumn(name = "cart_id"),
            inverseJoinColumns = @JoinColumn(name = "bouquet_id")
    )
    private List<Bouquet> bouquets = new ArrayList<>();
}