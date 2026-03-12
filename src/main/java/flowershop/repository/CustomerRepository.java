package flowershop.repository;

import flowershop.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c From Customer c " +
            "JOIN c.orders o " +
            "JOIN o.bouquets b " +
            "JOIN b.flowers f " +
            "WHERE f.id=:flowerId " +
            "AND CAST (o.status AS string) IN :status " +
            "AND o.deliveryDate=:date " +
            "GROUP BY c.id, o.deliveryDate"
    )
    Page<Customer> findByFlower(@Param("flowerId") Long flowerId,
                                       @Param("date") LocalDate date,
                                       @Param("status") List<String> statuses,
                                       Pageable pageable);

    @Query(value = "SELECT  c.* FROM customers c " +
            "JOIN orders o ON c.id = o.customer_id " +
            "JOIN order_bouquets ob ON o.id = ob.orders_id " +
            "JOIN bouquets b ON b.id = ob.bouquets_id " +
            "JOIN bouquet_flowers bf ON b.id = bf.bouquet_id " +
            "JOIN flower f ON f.id = bf.flower_id " +
            "WHERE f.id = :flowerId " +
            "AND o.status::text IN :statuses " +
            "AND o.delivery_date = :date " +
            "GROUP BY c.id, o.delivery_date",
            countQuery = "SELECT count(DISTINCT c.id) FROM customers c " +
                    "JOIN orders o ON c.id = o.customer_id " +
                    "JOIN order_bouquets ob ON o.id = ob.orders_id " +
                    "JOIN bouquets b ON b.id = ob.bouquets_id " +
                    "JOIN bouquet_flowers bf ON b.id = bf.bouquet_id " +
                    "JOIN flower f ON f.id = bf.flower_id " +
                    "WHERE f.id = :flowerId AND o.status::text IN :statuses " +
                    "AND o.delivery_date = :date",
            nativeQuery = true)
    Page<Customer> findByFlowerNative(@Param("flowerId") Long flowerId,
                                             @Param("date") LocalDate date,
                                             @Param("statuses") List<String> statuses,
                                             Pageable pageable);


}