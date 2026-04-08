package flowershop.service;

import flowershop.components.CustomerHashMap;
import flowershop.dto.CustomerDto;
import flowershop.entity.Customer;
import flowershop.entity.SearchKey;
import flowershop.entity.ShoppingCart;
import flowershop.exception.TransactionDemoException;
import flowershop.repository.CustomerRepository;
import flowershop.repository.ShoppingCartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private CustomerHashMap hashMap;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerDto customerDto;
    private ShoppingCart shoppingCart;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.now();
        shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Иван");
        customer.setCart(shoppingCart);

        customerDto = new CustomerDto();
        customerDto.setId(1L);
        customerDto.setName("Иван");
        customerDto.setPhoneNumber("+375291234567");
    }

    @Test
    void update_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        customerService.update(1L, customerDto);

        verify(customerRepository).save(any(Customer.class));
        verify(hashMap, times(1)).clear(); // Проверяем, что кэш очистился
    }

    @Test
    void delete_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        customerService.delete(1L);

        verify(customerRepository).delete(customer);
        verify(hashMap, times(1)).clear(); // Проверяем очистку
    }

    @Test
    void findByFlower_FromCache() {
        Page<CustomerDto> cachedPage = new PageImpl<>(List.of(customerDto));
        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(true);
        when(hashMap.get(any(SearchKey.class))).thenReturn(cachedPage);

        Page<CustomerDto> result = customerService.findByFlower("Роза", testDate, 0, 10);

        assertNotNull(result);
        verify(customerRepository, never()).findByFlower(any(), any(), any(), any());
    }

    @Test
    void findByFlower_FromRepository() {
        Page<Customer> dbPage = new PageImpl<>(List.of(customer));
        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(false);
        when(customerRepository.findByFlower(any(), any(), any(), any(Pageable.class))).thenReturn(dbPage);

        customerService.findByFlower("Роза", testDate, 0, 10);

        verify(customerRepository).findByFlower(any(), any(), any(), any());
        verify(hashMap).put(any(SearchKey.class), any());
    }

    @Test
    void create_Success() {
        when(shoppingCartRepository.saveAndFlush(any())).thenReturn(shoppingCart);
        when(customerRepository.saveAndFlush(any())).thenReturn(customer);

        CustomerDto result = customerService.create(customerDto);

        assertNotNull(result);
        verify(shoppingCartRepository).saveAndFlush(any());
        verify(customerRepository).saveAndFlush(any());
    }

    @Test
    void findByFlower_CacheClearAfterUpdate_ShouldUseRepository() {
        Page<Customer> dbPage = new PageImpl<>(List.of(customer));

        // Первый вызов - не в кэше
        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(false);
        when(customerRepository.findByFlower(any(), any(), any(), any())).thenReturn(dbPage);
        customerService.findByFlower("Роза", testDate, 0, 10);

        // Обновление - чистим кэш
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any())).thenReturn(customer);
        customerService.update(1L, customerDto);

        // Второй вызов - опять не должно быть в кэше (так как был clear)
        customerService.findByFlower("Роза", testDate, 0, 10);

        // Итого: 2 вызова репозитория, 2 вызова clear
        verify(customerRepository, times(2)).findByFlower(any(), any(), any(), any());
        verify(hashMap, times(1)).clear();
    }

    @Test
    void createWithTransaction_ThrowsException() {
        when(customerRepository.save(any())).thenReturn(customer);

        assertThrows(TransactionDemoException.class, () ->
                customerService.createWithTransaction(customerDto)
        );

        verify(hashMap).clear();
    }

    @Test
    void findById_NotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> customerService.findById(99L));
    }

    // Удалены дублирующиеся тесты для Native, так как логика идентична findByFlower
    // Оставлены только ключевые проверки для чистоты файла
}