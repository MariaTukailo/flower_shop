package flowershop.service;

import flowershop.dto.OrderDto;
import flowershop.entity.Bouquet;
import flowershop.entity.Order;
import flowershop.entity.ShoppingCart;
import flowershop.enums.OrderStatus;
import flowershop.mapper.OrderMapper;
import flowershop.repository.OrderRepository;
import flowershop.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import flowershop.entity.Customer;
import flowershop.repository.CustomerRepository;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    private Order findEntityById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<OrderDto> findAll() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    public OrderDto findById(Long id) {
        Order order = findEntityById(id);
        return OrderMapper.toDto(order);
    }



    @Transactional
    public OrderDto createFromCart(Long customerId) {

        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null || customer.getCart() == null) {
            return null;
        }

        ShoppingCart cart = customer.getCart();
        List<Bouquet> bouquetsInCart = cart.getBouquets();

        if (bouquetsInCart == null || bouquetsInCart.isEmpty()) {
            return null;
        }

        for (Bouquet bouquet : bouquetsInCart) {
            if (!bouquet.isActive()) {

                return null;
            }
        }


        Order order = new Order();
        order.setCustomer(customer);
        order.setDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PROCESSING);


        order.setBouquets(new ArrayList<>(bouquetsInCart));

        double total = 0;
        for (Bouquet bouquet : bouquetsInCart) {
            total += bouquet.getPrice();
        }
        order.setFinalPrice(total);

        cart.getBouquets().clear();
        shoppingCartRepository.save(cart);

        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto updateStatus(Long id, String statusValue) {
        Order order = findEntityById(id);

        OrderStatus newStatus = OrderStatus.fromString(statusValue);
        if (newStatus != null) {
            order.setStatus(newStatus);
        }

        return OrderMapper.toDto(orderRepository.save(order));
    }
}