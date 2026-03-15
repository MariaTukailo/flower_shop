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
        log.debug("Поиск всех покупателей ");
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(CustomerMapper::toDto)
                .toList();
    }

    public CustomerDto findById(Long id) {
        log.debug("Поиск  покупателей по ID : {} ", id);
        Customer customer = findEntityById(id);
        return CustomerMapper.toDto(customer);
    }

    @Transactional
    public CustomerDto createTransactional(CustomerDto dto) {
        log.info("Начало сохранения покупателя под id {}", dto.getId());
        Customer customer = customerRepository.save(CustomerMapper.toEntity(dto));

        log.debug("Создание корзины покупателя с ID : {} ", dto.getId());
        ShoppingCart cart = new ShoppingCart();
        cart.setCustomer(customer);
        cart.setId(customer.getId());

        customer.setCart(cart);
        log.info("Корзина покупателя успешно сохранен в БД под ID: {}", cart.getId());
        hashMap.clear();

        CustomerDto save = CustomerMapper.toDto(customerRepository.save(customer));
        log.info("Покупатель успешно сохранен в БД под ID: {}", save.getId());
        return save;
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

        log.info("Начало изменения покупателя под id {}", dto.getId());

        log.debug("Поиск  покупателя по ID : {} ", id);
        Customer customer = findEntityById(id);

        customer.setName(dto.getName());
        customer.setPhoneNumber(dto.getPhoneNumber());

        hashMap.clear();
        CustomerDto update = CustomerMapper.toDto(customerRepository.save(customer));
        log.info("Покупатель под id {} успешно изменен", dto.getId());
        return update;
    }

    @Transactional
    public void delete(Long id) {
        log.info("Начало удаления покупателя под id {}", id);
        log.debug("Поиск  покупателя  по ID : {} ", id);
        Customer customer = findEntityById(id);

        hashMap.clear();
        customerRepository.delete(customer);
        log.info("Покупатель с ID {} успешно удален ", id);
    }

<<<<<<< HEAD
    public Page<CustomerDto> findByFlower(Long flowerId, LocalDate date, int page, int size) {
        log.info("Начало поиска покупателей имеющих активные заказы, в которых содержится определенный цветок");
        List<String> orderStatuses = List.of(OrderStatus.PROCESSING.name(), OrderStatus.ACCEPTED.name());
        Pageable pageable = PageRequest.of(page, size, Sort.by("o.deliveryDate").descending());

        log.debug("Создание ключа");
        SearchKey key = new SearchKey(flowerId, orderStatuses, date, page, size);
=======
    public Page<CustomerDto> findByFlower(String flowerName, LocalDate date, int page, int size) {

        List<String> orderStatuses = List.of(OrderStatus.PROCESSING.name(), OrderStatus.ACCEPTED.name());
        Pageable pageable = PageRequest.of(page, size, Sort.by("o.deliveryDate").descending());

        SearchKey key = new SearchKey(flowerName, orderStatuses, date, page, size);
>>>>>>> 8e50425b6de151fde8d97fbab02caad86bf4bf12

        log.debug("Поиск операции в хеш-таблице");
        if (hashMap.containsKey(key)) {
            log.info("Данные взяты из кеша ");
            return hashMap.get(key);
        }

<<<<<<< HEAD
        log.debug("Выполнение поиска");
        Page<Customer> customers = customerRepository.findByFlower(flowerId, date, orderStatuses, pageable);
        log.debug("Преобразование в Dto");
=======
        Page<Customer> customers = customerRepository.findByFlower(flowerName, date, orderStatuses, pageable);
>>>>>>> 8e50425b6de151fde8d97fbab02caad86bf4bf12
        Page<CustomerDto> customersDto = customers.map(CustomerMapper::toDto);

        log.debug("Добавление операции в хеш-таблицу");
        hashMap.put(key, customersDto);
        log.info("Конец поиска покупателей имеющих активные заказы, в которых содержится определенный цветок");
        return customersDto;

    }

<<<<<<< HEAD
    public Page<CustomerDto> findByFlowerNative(Long flowerId, LocalDate date, int page, int size) {
        log.info("Начало поиска покупателей имеющих активные заказы,  в которых содержится определенный цветок");
        Pageable pageable = PageRequest.of(page, size, Sort.by("o.delivery_date").descending());
        List<String> orderStatuses = List.of(OrderStatus.PROCESSING.name(), OrderStatus.ACCEPTED.name());

        log.debug("Создание  ключа");
        SearchKey key = new SearchKey(flowerId, orderStatuses, date, page, size);
=======
    public Page<CustomerDto> findByFlowerNative(String flowerName, LocalDate date, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("o.delivery_date").descending());
        List<String> orderStatuses = List.of(OrderStatus.PROCESSING.name(), OrderStatus.ACCEPTED.name());


        SearchKey key = new SearchKey(flowerName, orderStatuses, date, page, size);
>>>>>>> 8e50425b6de151fde8d97fbab02caad86bf4bf12

        log.debug("Поиск  операции в хеш-таблице");
        if (hashMap.containsKey(key)) {
            log.info("Данные взяты из кеша");
            return hashMap.get(key);
        }

<<<<<<< HEAD
        log.debug("Выполнение  поиска");
        Page<Customer> customers = customerRepository.findByFlowerNative(flowerId, date, orderStatuses, pageable);
        log.debug("Преобразование в  Dto");
=======
        Page<Customer> customers = customerRepository.findByFlowerNative(flowerName, date, orderStatuses, pageable);
>>>>>>> 8e50425b6de151fde8d97fbab02caad86bf4bf12
        Page<CustomerDto> customersDto = customers.map(CustomerMapper::toDto);

        log.debug("Добавление операции  в хеш-таблицу");
        hashMap.put(key, customersDto);

        log.info("Конец  поиска покупателей имеющих активные заказы, в которых содержится определенный цветок");
        return customersDto;
    }
}
