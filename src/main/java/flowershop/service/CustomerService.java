package flowershop.service;

import flowershop.dto.CustomerDto;
import flowershop.entity.Customer;
import flowershop.entity.ShoppingCart;
import flowershop.exception.TransactionDemoException;
import flowershop.mapper.CustomerMapper;
import flowershop.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    private Customer findEntityById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    public List<CustomerDto> findAll() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(CustomerMapper::toDto)
                .toList();
    }

    public CustomerDto findById(Long id) {
        Customer customer = findEntityById(id);
        return CustomerMapper.toDto(customer);
    }

    @Transactional
    public CustomerDto createTransactional(CustomerDto dto) {

        Customer customer = CustomerMapper.toEntity(dto);
        customer = customerRepository.save(customer);

        ShoppingCart cart = new ShoppingCart();
        cart.setCustomer(customer);
        cart.setId(customer.getId());

        customer.setCart(cart);

        return CustomerMapper.toDto(customerRepository.save(customer));
    }

    public CustomerDto createWithoutTransaction(CustomerDto dto) {
        Customer customer = CustomerMapper.toEntity(dto);
        customer = customerRepository.save(customer);

        ShoppingCart cart = new ShoppingCart();
        cart.setCustomer(customer);
        cart.setId(customer.getId());

        customer.setCart(cart);

        throw new TransactionDemoException("Тест: Ошибка БЕЗ @Transactional.");
    }


    @Transactional
    public CustomerDto createWithTransaction(CustomerDto dto) {
        Customer customer = CustomerMapper.toEntity(dto);
        customer = customerRepository.save(customer);

        ShoppingCart cart = new ShoppingCart();
        cart.setCustomer(customer);
        cart.setId(customer.getId());

        customer.setCart(cart);



        throw new TransactionDemoException("Тест: Ошибка С @Transactional.");
    }

    @Transactional
    public CustomerDto update(Long id, CustomerDto dto) {
        Customer customer = findEntityById(id);

        customer.setName(dto.getName());
        customer.setPhoneNumber(dto.getPhoneNumber());

        return CustomerMapper.toDto(customerRepository.save(customer));
    }

    @Transactional
    public void delete(Long id) {
        Customer customer = findEntityById(id);

        customerRepository.delete(customer);

    }
}
