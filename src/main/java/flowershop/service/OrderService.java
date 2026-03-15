package flowershop.service;

import flowershop.components.CustomerHashMap;
import flowershop.dto.OrderDto;
import flowershop.entity.Bouquet;
import flowershop.entity.Order;
import flowershop.entity.ShoppingCart;
import flowershop.enums.OrderStatus;
import flowershop.mapper.OrderMapper;
import flowershop.repository.OrderRepository;
import flowershop.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import flowershop.entity.Customer;
import flowershop.repository.CustomerRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CustomerHashMap hashMap;

    private Order findEntityById(Long id) {

        log.debug("Поиск сущности заказа  по ID {}", id);
        return orderRepository.findById(id).orElse(null);
    }

    public List<OrderDto> findAll() {
        log.debug("Поиск всех заказов");
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    public OrderDto findById(Long id) {
        log.debug("Поиск  заказа  по ID {}", id);
        Order order = findEntityById(id);
        return OrderMapper.toDto(order);
    }


    @Transactional
    public OrderDto createFromCart(Long customerId, LocalDate deliveryDate, LocalTime deliveryTime) {

        log.info("Начало сохранения заказа пользователя под id {}", customerId);

        log.debug("проверка на пустоту даты и времени");
        if (deliveryDate == null || deliveryTime == null) {
            log.warn("Дата или время пустое. заказ не создан");
            return null;
        }

        log.debug("Поиск по id покупателя , делающего заказ , с id : {}", customerId);
        Customer customer = customerRepository.findById(customerId).orElse(null);
        log.debug("проверка на пустоту покупателя и корзины");
        if (customer == null || customer.getCart() == null) {
            log.warn("Покупатель или корзина  пустые. заказ не создан");
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
        order.setDeliveryDate(deliveryDate);
        order.setDeliveryTime(deliveryTime);

        order.setBouquets(new ArrayList<>(bouquetsInCart));

        double total = 0;
        for (Bouquet bouquet : bouquetsInCart) {
            total += bouquet.getPrice();
        }
        order.setFinalPrice(total);

        cart.getBouquets().clear();
        shoppingCartRepository.save(cart);

        hashMap.clear();
        OrderDto saveOrder = OrderMapper.toDto(orderRepository.save(order));
        log.info(" Заказ пользователя под id {} успешно создан", customerId);
        return saveOrder;
    }

    @Transactional
    public OrderDto updateStatus(Long id, String statusValue) {

        log.info("Начало изменения статуса заказа под id {}", id);
        log.debug("Поиск заказа по ID: {}", id);
        Order order = findEntityById(id);


        OrderStatus newStatus = OrderStatus.fromString(statusValue);
        if (newStatus != null) {
            order.setStatus(newStatus);
        }

        hashMap.clear();
        OrderDto updateOrder = OrderMapper.toDto(orderRepository.save(order));
        log.info("Изменение статуса заказа под id {} успешно завершено", id);
        return updateOrder;
    }
}