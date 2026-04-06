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
        assertEquals("ivan123", response.getUsername());
        assertEquals("USER", response.getRole());
        assertEquals(1L, response.getCustomerId());
        verify(userRepository, times(1)).findByUsername("ivan123");
    }

    @Test
    void authenticate_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        loginRequest.setUsername("unknown");

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.authenticate(loginRequest)
        );

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("unknown");
    }

    @Test
    void authenticate_WrongPassword() {
        when(userRepository.findByUsername("ivan123")).thenReturn(Optional.of(user));
        loginRequest.setPassword("wrongpassword");

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.authenticate(loginRequest)
        );

        assertEquals("Неверный пароль", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("ivan123");
    }

    @Test
    void authenticate_UserWithoutCustomer() {
        User userWithoutCustomer = new User();
        userWithoutCustomer.setId(2L);
        userWithoutCustomer.setUsername("admin");
        userWithoutCustomer.setPassword("admin123");
        userWithoutCustomer.setRole(Role.ADMIN);
        userWithoutCustomer.setCustomer(null);

        LoginRequest adminLogin = new LoginRequest();
        adminLogin.setUsername("admin");
        adminLogin.setPassword("admin123");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(userWithoutCustomer));

        AuthResponse response = userService.authenticate(adminLogin);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("admin", response.getUsername());
        assertEquals("ADMIN", response.getRole());
        assertNull(response.getCustomerId());
        verify(userRepository, times(1)).findByUsername("admin");
    }

    @Test
    void register_Success() {
        Customer savedCustomer = new Customer();
        savedCustomer.setId(2L);
        savedCustomer.setName("Петр Петров");
        savedCustomer.setPhoneNumber("+375297654321");

        ShoppingCart savedCart = new ShoppingCart();
        savedCart.setId(2L);
        savedCart.setBouquets(new ArrayList<>());
        savedCustomer.setCart(savedCart);
        savedCart.setCustomer(savedCustomer);

        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setUsername("petr456");
        savedUser.setPassword("password456");
        savedUser.setRole(Role.USER);
        savedUser.setCustomer(savedCustomer);

        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(savedCart);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        AuthResponse response = userService.register(registrationRequest);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("petr456", response.getUsername());
        assertEquals("USER", response.getRole());
        assertEquals(2L, response.getCustomerId());

        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_WithEmptyCart() {
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenAnswer(invocation -> {
            ShoppingCart newShoppingCart = invocation.getArgument(0);
            newShoppingCart.setId(3L);
            return newShoppingCart;
        });

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User newUser = invocation.getArgument(0);
            newUser.setId(3L);
            return newUser;
        });

        AuthResponse response = userService.register(registrationRequest);

        assertNotNull(response);
        assertEquals(3L, response.getId());
        assertEquals("USER", response.getRole());

        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteUser_SuccessWithCustomer() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).detachCustomer(1L);
        doNothing().when(customerRepository).deleteById(1L);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).detachCustomer(1L);
        verify(customerRepository, times(1)).deleteById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_UserWithoutCustomer() {
        User userWithoutCustomer = new User();
        userWithoutCustomer.setId(2L);
        userWithoutCustomer.setUsername("admin");
        userWithoutCustomer.setPassword("admin123");
        userWithoutCustomer.setRole(Role.ADMIN);
        userWithoutCustomer.setCustomer(null);

        when(userRepository.findById(2L)).thenReturn(Optional.of(userWithoutCustomer));
        doNothing().when(userRepository).deleteById(2L);

        userService.deleteUser(2L);

        verify(userRepository, times(1)).findById(2L);
        verify(userRepository, never()).detachCustomer(any());
        verify(customerRepository, never()).deleteById(any());
        verify(userRepository, times(1)).deleteById(2L);
    }

    @Test
    void deleteUser_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.deleteUser(99L)
        );

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository, times(1)).findById(99L);
        verify(userRepository, never()).detachCustomer(any());
        verify(customerRepository, never()).deleteById(any());
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void deleteUser_CustomerNotFoundButUserHasCustomerId() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).detachCustomer(1L);
        doThrow(new RuntimeException("Customer not found")).when(customerRepository).deleteById(1L);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.deleteUser(1L)
        );

        assertEquals("Customer not found", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).detachCustomer(1L);
        verify(customerRepository, times(1)).deleteById(1L);
        verify(userRepository, never()).deleteById(1L);
    }

    @Test
    void authenticate_NullUsername() {
        loginRequest.setUsername(null);

        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.authenticate(loginRequest)
        );

        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void authenticate_NullPassword() {
        when(userRepository.findByUsername("ivan123")).thenReturn(Optional.of(user));
        loginRequest.setPassword(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.authenticate(loginRequest)
        );

        assertEquals("Неверный пароль", exception.getMessage());
    }

    @Test
    void register_WithMinimalFields() {
        RegistrationRequest minimalRequest = new RegistrationRequest();
        minimalRequest.setUsername("test");
        minimalRequest.setPassword("test123");
        minimalRequest.setName("Test User");
        minimalRequest.setPhone("+375290000000");

        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenAnswer(invocation -> {
            ShoppingCart newCart = invocation.getArgument(0);
            newCart.setId(5L);
            return newCart;
        });

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            userToSave.setId(5L);
            return userToSave;
        });

        AuthResponse response = userService.register(minimalRequest);

        assertNotNull(response);
        assertEquals(5L, response.getId());
        assertEquals("test", response.getUsername());
        assertEquals("USER", response.getRole());
        assertNotNull(response.getCustomerId());
    }
}