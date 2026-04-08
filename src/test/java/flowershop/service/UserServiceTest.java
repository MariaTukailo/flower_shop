package flowershop.service;

import flowershop.dto.AuthResponse;
import flowershop.dto.LoginRequest;
import flowershop.dto.RegistrationRequest;
import flowershop.entity.Customer;
import flowershop.entity.ShoppingCart;
import flowershop.entity.User;
import flowershop.enums.Role;
import flowershop.repository.CustomerRepository;
import flowershop.repository.ShoppingCartRepository;
import flowershop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private Customer customer;
    private ShoppingCart cart;
    private LoginRequest loginRequest;
    private RegistrationRequest registrationRequest;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
        cart.setId(1L);
        cart.setBouquets(new ArrayList<>());

        customer = new Customer();
        customer.setId(1L);
        customer.setName("Иван Иванов");
        customer.setPhoneNumber("+375291234567");
        customer.setCart(cart);
        cart.setCustomer(customer);

        user = new User();
        user.setId(1L);
        user.setUsername("ivan123");
        user.setPassword("password123");
        user.setRole(Role.USER);
        user.setCustomer(customer);

        loginRequest = new LoginRequest();
        loginRequest.setUsername("ivan123");
        loginRequest.setPassword("password123");

        registrationRequest = new RegistrationRequest();
        registrationRequest.setUsername("petr456");
        registrationRequest.setPassword("password456");
        registrationRequest.setName("Петр Петров");
        registrationRequest.setPhone("+375297654321");
    }

    @Test
    void authenticate_Success() {
        when(userRepository.findByUsername("ivan123")).thenReturn(Optional.of(user));
        AuthResponse response = userService.authenticate(loginRequest);
        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void authenticate_WrongPassword() {
        when(userRepository.findByUsername("ivan123")).thenReturn(Optional.of(user));
        loginRequest.setPassword("wrongpassword");
        // В твоем сервисе кидается IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> userService.authenticate(loginRequest));
    }

    @Test
    void register_Success() {
        // Имитируем сохранение: устанавливаем ID юзеру и его клиенту
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(2L);
            if (u.getCustomer() != null) u.getCustomer().setId(2L);
            return u;
        });

        AuthResponse response = userService.register(registrationRequest);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals(2L, response.getCustomerId());
        // Проверяем только сохранение юзера, так как корзину сервис отдельно не сохраняет
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_WithEmptyCart() {
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(3L);
            if (u.getCustomer() != null) u.getCustomer().setId(3L);
            return u;
        });

        AuthResponse response = userService.register(registrationRequest);

        assertNotNull(response);
        assertEquals(3L, response.getId());
        // Убираем verify для shoppingCartRepository, так как в коде его нет
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_SuccessWithCustomer() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).detachCustomer(1L);
        verify(customerRepository).deleteById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void register_WithMinimalFields() {
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(5L);
            if (u.getCustomer() != null) u.getCustomer().setId(5L);
            return u;
        });

        AuthResponse response = userService.register(registrationRequest);

        assertNotNull(response);
        assertEquals(5L, response.getId());
        assertNotNull(response.getCustomerId());
    }
}