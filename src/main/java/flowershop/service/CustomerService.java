package flowershop.service;

import flowershop.components.CustomerHashMap;
import flowershop.dto.CustomerDto;
import flowershop.entity.Customer;
import flowershop.entity.SearchKey;
import flowershop.entity.ShoppingCart;
import flowershop.enums.OrderStatus;
import flowershop.exception.TransactionDemoException;
import flowershop.mapper.CustomerMapper;
import flowershop.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerHashMap hashMap;

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

        Customer customer = customerRepository.save(CustomerMapper.toEntity(dto));

        ShoppingCart cart = new ShoppingCart();
        cart.setCustomer(customer);
        cart.setId(customer.getId());

        customer.setCart(cart);

        hashMap.clear();

        return CustomerMapper.toDto(customerRepository.save(customer));
    }

    public CustomerDto createWithoutTransaction(CustomerDto dto) {
        Customer customer = customerRepository.save(CustomerMapper.toEntity(dto));

        ShoppingCart cart = new ShoppingCart();
        cart.setCustomer(customer);
        cart.setId(customer.getId());

        customer.setCart(cart);

        hashMap.clear();

        throw new TransactionDemoException("Тест: Ошибка БЕЗ @Transactional.");
    }


    @Transactional
    public CustomerDto createWithTransaction(CustomerDto dto) {
        Customer customer = customerRepository.save(CustomerMapper.toEntity(dto));

        ShoppingCart cart = new ShoppingCart();
        cart.setCustomer(customer);
        cart.setId(customer.getId());

        customer.setCart(cart);

        hashMap.clear();

        throw new TransactionDemoException("Тест: Ошибка С @Transactional.");
    }

    @Transactional
    public CustomerDto update(Long id, CustomerDto dto) {
        Customer customer = findEntityById(id);


        customer.setName(dto.getName());
        customer.setPhoneNumber(dto.getPhoneNumber());

        hashMap.clear();

        return CustomerMapper.toDto(customerRepository.save(customer));
    }

    @Transactional
    public void delete(Long id) {
        Customer customer = findEntityById(id);

        hashMap.clear();
        customerRepository.delete(customer);

    }

    public Page<CustomerDto> findByFlower(String flowerName, LocalDate date, int page, int size) {

        List<String> orderStatuses = List.of(OrderStatus.PROCESSING.name(), OrderStatus.ACCEPTED.name());
        Pageable pageable = PageRequest.of(page, size, Sort.by("o.deliveryDate").descending());

        SearchKey key = new SearchKey(flowerName, orderStatuses, date, page, size);

        if (hashMap.containsKey(key)) {
            log.info("Данные взяты из кеша ");
            return hashMap.get(key);
        }

        Page<Customer> customers = customerRepository.findByFlower(flowerName, date, orderStatuses, pageable);
        Page<CustomerDto> customersDto = customers.map(CustomerMapper::toDto);

        hashMap.put(key, customersDto);

        return customersDto;

    }

    public Page<CustomerDto> findByFlowerNative(String flowerName, LocalDate date, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("o.delivery_date").descending());
        List<String> orderStatuses = List.of(OrderStatus.PROCESSING.name(), OrderStatus.ACCEPTED.name());


        SearchKey key = new SearchKey(flowerName, orderStatuses, date, page, size);

        if (hashMap.containsKey(key)) {
            log.info("Данные взяты из кеша");
            return hashMap.get(key);
        }

        Page<Customer> customers = customerRepository.findByFlowerNative(flowerName, date, orderStatuses, pageable);
        Page<CustomerDto> customersDto = customers.map(CustomerMapper::toDto);

        hashMap.put(key, customersDto);
        return customersDto;
    }
}
