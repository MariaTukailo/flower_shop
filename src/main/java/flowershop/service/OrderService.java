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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import flowershop.entity.Customer;
import flowershop.repository.CustomerRepository;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CustomerHashMap hashMap;

    private Order findEntityById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ не найден"));
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
    public OrderDto createFromCart(Long customerId, LocalDate deliveryDate, LocalTime deliveryTime, String address) {

        log.info("Начало сохранения заказа пользователя под id {}", customerId);

        log.debug("проверка на пустоту даты и времени");
        Optional.ofNullable(deliveryDate).orElseThrow(() -> new IllegalArgumentException("Дата доставки обязательна"));
        Optional.ofNullable(deliveryTime).orElseThrow(() -> new IllegalArgumentException("Время доставки обязательно"));

        log.debug("Поиск по id покупателя , делающего заказ , с id : {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Покупатель не найден"));


        log.debug("проверка на пустоту покупателя и корзины");
        ShoppingCart cart = Optional.ofNullable(customer.getCart())
                .orElseThrow(() -> new IllegalStateException("У покупателя нет корзины"));


        List<Bouquet> bouquetsInCart = cart.getBouquets();

        if (bouquetsInCart == null || bouquetsInCart.isEmpty()) {
            throw new IllegalStateException("Корзина пуста");
        }

        boolean hasInactive = bouquetsInCart.stream().anyMatch(b -> !b.isActive());
        if (hasInactive) {
            throw new IllegalStateException("В корзине есть неактивные букеты");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PROCESSING);
        order.setDeliveryDate(deliveryDate);
        order.setDeliveryTime(deliveryTime);
        order.setAddress(address);

        order.setBouquets(new ArrayList<>(bouquetsInCart));

        double total = bouquetsInCart.stream()
                .mapToDouble(Bouquet::getPrice)
                .sum();


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

        OrderStatus newStatus = Optional.ofNullable(OrderStatus.fromString(statusValue))
                .orElseThrow(() -> new IllegalArgumentException("Некорректный статус: " + statusValue));

        order.setStatus(newStatus);

        hashMap.clear();
        OrderDto updateOrder = OrderMapper.toDto(orderRepository.save(order));
        log.info("Изменение статуса заказа под id {} успешно завершено", id);
        return updateOrder;
    }


}