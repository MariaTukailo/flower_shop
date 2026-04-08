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

import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

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
        customer.setPhoneNumber("+375291234567");
        customer.setCart(shoppingCart);

        customerDto = new CustomerDto();
        customerDto.setId(1L);
        customerDto.setName("Иван");
        customerDto.setPhoneNumber("+375291234567");
    }

    @Test
    void findAll_Success() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));
        List<CustomerDto> result = customerService.findAll();
        assertEquals(1, result.size());
        verify(customerRepository).findAll();
    }

    @Test
    void findById_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        CustomerDto result = customerService.findById(1L);
        assertEquals("Иван", result.getName());
    }

    @Test
    void findById_NotFound_ThrowsException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> customerService.findById(99L));
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
    void createWithoutTransaction_ThrowsException() {
        when(customerRepository.save(any())).thenReturn(customer);
        assertThrows(TransactionDemoException.class, () -> customerService.createWithoutTransaction(customerDto));
        verify(hashMap).clear();
    }

    @Test
    void createWithTransaction_ThrowsException() {
        when(customerRepository.save(any())).thenReturn(customer);
        assertThrows(TransactionDemoException.class, () -> customerService.createWithTransaction(customerDto));
        verify(hashMap).clear();
    }

    @Test
    void update_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any())).thenReturn(customer);

        CustomerDto result = customerService.update(1L, customerDto);

        assertNotNull(result);
        verify(hashMap).clear();
        verify(customerRepository).save(any());
    }

    @Test
    void delete_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        customerService.delete(1L);
        verify(customerRepository).delete(customer);
        verify(hashMap).clear();
    }



    @Test
    void findByFlower_CacheHit() {
        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(true);
        when(hashMap.get(any(SearchKey.class))).thenReturn(new PageImpl<>(List.of(customerDto)));

        Page<CustomerDto> result = customerService.findByFlower("Роза", testDate, 0, 10);

        assertFalse(result.isEmpty());
        verify(customerRepository, never()).findByFlower(any(), any(), any(), any());
    }

    @Test
    void findByFlower_CacheMiss() {
        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(false);
        when(customerRepository.findByFlower(any(), any(), any(), any())).thenReturn(new PageImpl<>(List.of(customer)));

        customerService.findByFlower("Роза", testDate, 0, 10);

        verify(customerRepository).findByFlower(any(), any(), any(), any());
        verify(hashMap).put(any(SearchKey.class), any());
    }



    @Test
    void findByFlowerNative_CacheHit() {
        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(true);
        when(hashMap.get(any(SearchKey.class))).thenReturn(new PageImpl<>(List.of(customerDto)));

        Page<CustomerDto> result = customerService.findByFlowerNative("Лилия", testDate, 0, 10);

        assertFalse(result.isEmpty());
        verify(customerRepository, never()).findByFlowerNative(any(), any(), any(), any());
    }

    @Test
    void findByFlowerNative_CacheMiss() {
        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(false);
        when(customerRepository.findByFlowerNative(any(), any(), any(), any())).thenReturn(new PageImpl<>(List.of(customer)));

        customerService.findByFlowerNative("Лилия", testDate, 0, 10);

        verify(customerRepository).findByFlowerNative(any(), any(), any(), any());
        verify(hashMap).put(any(SearchKey.class), any());
    }
}