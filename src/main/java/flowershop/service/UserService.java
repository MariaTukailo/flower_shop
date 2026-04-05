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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    public AuthResponse authenticate(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Неверный пароль");
        }

        AuthResponse response = new AuthResponse();

        response.setId(user.getId());

        response.setUsername(user.getUsername());
        response.setRole(user.getRole().name());

        if (user.getCustomer() != null) {
            response.setCustomerId(user.getCustomer().getId());
        }

        return response;
    }

    @Transactional
    public AuthResponse register(RegistrationRequest request) {

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setPhoneNumber(request.getPhone());


        ShoppingCart cart = new ShoppingCart();
        cart.setBouquets(new ArrayList<>());


        customer.setCart(cart);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(Role.USER);
        user.setCustomer(customer);


        userRepository.save(user);

        return new AuthResponse(user.getId(), user.getUsername(), user.getRole().name(), customer.getId());
    }

    @Transactional
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (user.getCustomer() != null) {
            Long customerId = user.getCustomer().getId();


            userRepository.detachCustomer(userId);


            customerRepository.deleteById(customerId);
        }


        userRepository.deleteById(userId);
    }

}