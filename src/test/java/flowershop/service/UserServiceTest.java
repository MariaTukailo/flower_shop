package flowershop.service;

import flowershop.dto.AuthResponse;
import flowershop.dto.LoginRequest;
import flowershop.dto.RegistrationRequest;
import flowershop.entity.Customer;

import flowershop.entity.User;
import flowershop.enums.Role;
import flowershop.repository.CustomerRepository;
import flowershop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    private UserService userService;

    private User user;
    private Customer customer;
    private LoginRequest loginRequest;
    private RegistrationRequest registrationRequest;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(10L);
        customer.setName("Иван");

        user = new User();
        user.setId(1L);
        user.setUsername("ivan");
        user.setPassword("pass");
        user.setRole(Role.USER);
        user.setCustomer(customer);

        loginRequest = new LoginRequest();
        loginRequest.setUsername("ivan");
        loginRequest.setPassword("pass");

        registrationRequest = new RegistrationRequest();
        registrationRequest.setUsername("new");
        registrationRequest.setPassword("newPass");
        registrationRequest.setName("Name");
        registrationRequest.setPhone("123");
    }



    @Test
    void authenticate_Success() {
        when(userRepository.findByUsername("ivan")).thenReturn(Optional.of(user));
        AuthResponse response = userService.authenticate(loginRequest);
        assertEquals(10L, response.getCustomerId());
    }

    @Test
    void authenticate_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.authenticate(loginRequest));
    }

    @Test
    void authenticate_WrongPassword_ThrowsException() {
        when(userRepository.findByUsername("ivan")).thenReturn(Optional.of(user));
        loginRequest.setPassword("wrong");
        assertThrows(IllegalArgumentException.class, () -> userService.authenticate(loginRequest));
    }

    @Test
    void authenticate_NoCustomer_Success() {
        user.setCustomer(null);
        when(userRepository.findByUsername("ivan")).thenReturn(Optional.of(user));
        AuthResponse response = userService.authenticate(loginRequest);
        assertNull(response.getCustomerId());
    }



    @Test
    void register_Success() {
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            u.getCustomer().setId(100L);
            return u;
        });

        AuthResponse response = userService.register(registrationRequest);

        assertNotNull(response);
        assertEquals(100L, response.getCustomerId());
        verify(userRepository).save(any());
    }



    @Test
    void deleteUser_WithCustomer_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).detachCustomer(1L);
        verify(customerRepository).deleteById(10L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_WithoutCustomer_Success() {
        user.setCustomer(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository, never()).detachCustomer(any());
        verify(customerRepository, never()).deleteById(any());
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.deleteUser(99L));
    }
}