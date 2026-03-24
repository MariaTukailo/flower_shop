package flowershop.service;

import flowershop.components.CustomerHashMap;
import flowershop.dto.OrderDto;
import flowershop.entity.Bouquet;
import flowershop.entity.Customer;
import flowershop.entity.Order;
import flowershop.entity.ShoppingCart;
import flowershop.enums.OrderStatus;
import flowershop.repository.CustomerRepository;
import flowershop.repository.OrderRepository;
import flowershop.repository.ShoppingCartRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private CustomerHashMap hashMap;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;
    private ShoppingCart cart;
    private Bouquet bouquet;

    @BeforeEach
    void setUp() {
        bouquet = new Bouquet();
        bouquet.setId(1L);
        bouquet.setPrice(100.0);
        bouquet.setActive(true);

        cart = new ShoppingCart();
        cart.setId(1L);
        cart.setBouquets(new ArrayList<>(List.of(bouquet)));

        customer = new Customer();
        customer.setId(1L);
        customer.setCart(cart);
    }

    @Test
    void findAll_Success() {
        when(orderRepository.findAll()).thenReturn(List.of(new Order()));
        List<OrderDto> result = orderService.findAll();
        assertFalse(result.isEmpty());
    }

    @Test
    void findById_Success() {
        Order order = new Order();
        order.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        OrderDto result = orderService.findById(1L);
        assertNotNull(result);
    }

    @Test
    void findById_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> orderService.findById(1L));
    }

    @Test
    void createFromCart_Success() {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        OrderDto result = orderService.createFromCart(1L, date, time);

        assertNotNull(result);
        verify(shoppingCartRepository).save(any());
        verify(hashMap).clear();
    }

    @Test
    void createFromCart_NullDateTime() {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

        assertThrows(IllegalArgumentException.class, () ->
                orderService.createFromCart(1L, null, time)
        );

        assertThrows(IllegalArgumentException.class, () ->
                orderService.createFromCart(1L, date, null)
        );
    }

    @Test
    void createFromCart_CustomerNotFound() {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                orderService.createFromCart(1L, date, time)
        );
    }

    @Test
    void createFromCart_EmptyCart() {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        cart.getBouquets().clear();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        assertThrows(IllegalStateException.class, () ->
                orderService.createFromCart(1L, date, time)
        );
    }

    @Test
    void createFromCart_InactiveBouquets() {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        bouquet.setActive(false);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        assertThrows(IllegalStateException.class, () ->
                orderService.createFromCart(1L, date, time)
        );
    }

    @Test
    void updateStatus_Success() {
        Order order = new Order();
        order.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDto result = orderService.updateStatus(1L, "PROCESSING");

        assertNotNull(result);
        verify(hashMap).clear();
    }

    @Test
    void updateStatus_InvalidStatus() {
        Order order = new Order();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        assertThrows(IllegalArgumentException.class, () -> orderService.updateStatus(1L, "INVALID"));
    }

    @Test
    void createFromCart_PriceCalculationWithMultipleBouquets() {
        Bouquet bouquet2 = new Bouquet();
        bouquet2.setId(2L);
        bouquet2.setPrice(150.0);
        bouquet2.setActive(true);
        cart.getBouquets().add(bouquet2);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            assertEquals(250.0, savedOrder.getFinalPrice());
            return savedOrder;
        });

        orderService.createFromCart(1L, LocalDate.now(), LocalTime.now());

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createFromCart_CartWithNullBouquetsList() {
        cart.setBouquets(null);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));


        LocalDate deliveryDate = LocalDate.now();
        LocalTime deliveryTime = LocalTime.now();


        assertThrows(IllegalStateException.class, () ->
                orderService.createFromCart(1L, deliveryDate, deliveryTime)
        );
    }

    @Test
    void updateStatus_StatusCaseInsensitive() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDto result = orderService.updateStatus(1L, "processing");

        assertNotNull(result);
        verify(orderRepository).save(argThat(o -> o.getStatus() == OrderStatus.PROCESSING));
    }

    @Test
    void updateStatus_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> orderService.updateStatus(1L, "COMPLETED"));

    }
}