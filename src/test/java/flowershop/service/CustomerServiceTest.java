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
import org.springframework.data.domain.PageRequest;
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
        shoppingCart.setBouquets(new ArrayList<>());

        customer = new Customer();
        customer.setId(1L);
        customer.setName("Иван");
        customer.setPhoneNumber("+375291234567");
        customer.setCart(shoppingCart);
        shoppingCart.setCustomer(customer);

        customerDto = new CustomerDto();
        customerDto.setId(1L);
        customerDto.setName("Иван");
        customerDto.setPhoneNumber("+375291234567");
    }

    @Test
    void findAll_Success() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));

        List<CustomerDto> result = customerService.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Иван", result.get(0).getName());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void findAll_EmptyList() {
        when(customerRepository.findAll()).thenReturn(List.of());

        List<CustomerDto> result = customerService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void findById_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerDto result = customerService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Иван", result.getName());
        assertEquals("+375291234567", result.getPhoneNumber());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> customerService.findById(99L));
        verify(customerRepository, times(1)).findById(99L);
    }

    @Test
    void create_Success() {
        Customer newCustomer = new Customer();
        newCustomer.setId(2L);
        newCustomer.setName("Петр");
        newCustomer.setPhoneNumber("+375297654321");

        ShoppingCart newCart = new ShoppingCart();
        newCart.setId(2L);
        newCart.setBouquets(new ArrayList<>());

        CustomerDto inputDto = new CustomerDto();
        inputDto.setName("Петр");
        inputDto.setPhoneNumber("+375297654321");

        when(shoppingCartRepository.saveAndFlush(any(ShoppingCart.class))).thenReturn(newCart);
        when(customerRepository.saveAndFlush(any(Customer.class))).thenReturn(newCustomer);

        CustomerDto result = customerService.create(inputDto);

        assertNotNull(result);
        verify(shoppingCartRepository, times(1)).saveAndFlush(any(ShoppingCart.class));
        verify(customerRepository, times(1)).saveAndFlush(any(Customer.class));
    }

    @Test
    void createWithoutTransaction_ThrowsException() {
        CustomerDto inputDto = new CustomerDto();
        inputDto.setName("Петр");
        inputDto.setPhoneNumber("+375297654321");

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        assertThrows(TransactionDemoException.class, () ->
                customerService.createWithoutTransaction(inputDto)
        );

        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(hashMap, times(1)).clear();
    }

    @Test
    void createWithTransaction_ThrowsException() {
        CustomerDto inputDto = new CustomerDto();
        inputDto.setName("Петр");
        inputDto.setPhoneNumber("+375297654321");

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        assertThrows(TransactionDemoException.class, () ->
                customerService.createWithTransaction(inputDto)
        );

        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(hashMap, times(1)).clear();
    }

    @Test
    void update_Success() {
        CustomerDto updateDto = new CustomerDto();
        updateDto.setId(1L);
        updateDto.setName("Иван Петрович");
        updateDto.setPhoneNumber("+375293333333");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDto result = customerService.update(1L, updateDto);

        assertNotNull(result);
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(hashMap, times(1)).clear();
    }

    @Test
    void update_NotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () ->
                customerService.update(99L, customerDto)
        );

        verify(customerRepository, times(1)).findById(99L);
        verify(customerRepository, never()).save(any(Customer.class));
        verify(hashMap, never()).clear();
    }

    @Test
    void delete_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).delete(customer);

        customerService.delete(1L);

        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).delete(customer);
        verify(hashMap, times(1)).clear();
    }

    @Test
    void delete_NotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> customerService.delete(99L));

        verify(customerRepository, times(1)).findById(99L);
        verify(customerRepository, never()).delete(any(Customer.class));
        verify(hashMap, never()).clear();
    }

    @Test
    void findByFlower_FromCache() {
        Page<CustomerDto> cachedPage = new PageImpl<>(List.of(customerDto));

        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(true);
        when(hashMap.get(any(SearchKey.class))).thenReturn(cachedPage);

        Page<CustomerDto> result = customerService.findByFlower("Роза", testDate, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(hashMap, times(1)).containsKey(any(SearchKey.class));
        verify(hashMap, times(1)).get(any(SearchKey.class));
        verify(customerRepository, never()).findByFlower(any(), any(), any(), any(Pageable.class));
    }

    @Test
    void findByFlower_FromRepository() {
        Page<Customer> dbPage = new PageImpl<>(List.of(customer));

        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(false);
        when(customerRepository.findByFlower(any(), any(), any(), any(Pageable.class))).thenReturn(dbPage);

        Page<CustomerDto> result = customerService.findByFlower("Роза", testDate, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(customerRepository, times(1)).findByFlower(any(), any(), any(), any(Pageable.class));
        verify(hashMap, times(1)).put(any(SearchKey.class), any(Page.class));
    }

    @Test
    void findByFlower_EmptyResult() {
        Page<Customer> emptyPage = new PageImpl<>(List.of());

        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(false);
        when(customerRepository.findByFlower(any(), any(), any(), any(Pageable.class))).thenReturn(emptyPage);

        Page<CustomerDto> result = customerService.findByFlower("НесуществующийЦветок", testDate, 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(customerRepository, times(1)).findByFlower(any(), any(), any(), any(Pageable.class));
        verify(hashMap, times(1)).put(any(SearchKey.class), any(Page.class));
    }

    @Test
    void findByFlower_DifferentPageAndSize() {
        Page<Customer> dbPage = new PageImpl<>(List.of(customer));

        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(false);
        when(customerRepository.findByFlower(any(), any(), any(), any(Pageable.class))).thenReturn(dbPage);

        Page<CustomerDto> result = customerService.findByFlower("Роза", testDate, 2, 20);

        assertNotNull(result);
        verify(customerRepository, times(1)).findByFlower(any(), any(), any(), any(Pageable.class));
    }

    @Test
    void findByFlowerNative_FromCache() {
        Page<CustomerDto> cachedPage = new PageImpl<>(List.of(customerDto));

        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(true);
        when(hashMap.get(any(SearchKey.class))).thenReturn(cachedPage);

        Page<CustomerDto> result = customerService.findByFlowerNative("Лилия", testDate, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(hashMap, times(1)).containsKey(any(SearchKey.class));
        verify(hashMap, times(1)).get(any(SearchKey.class));
        verify(customerRepository, never()).findByFlowerNative(any(), any(), any(), any(Pageable.class));
    }

    @Test
    void findByFlowerNative_FromRepository() {
        Page<Customer> dbPage = new PageImpl<>(List.of(customer));

        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(false);
        when(customerRepository.findByFlowerNative(any(), any(), any(), any(Pageable.class))).thenReturn(dbPage);

        Page<CustomerDto> result = customerService.findByFlowerNative("Лилия", testDate, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(customerRepository, times(1)).findByFlowerNative(any(), any(), any(), any(Pageable.class));
        verify(hashMap, times(1)).put(any(SearchKey.class), any(Page.class));
    }

    @Test
    void findByFlowerNative_EmptyResult() {
        Page<Customer> emptyPage = new PageImpl<>(List.of());

        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(false);
        when(customerRepository.findByFlowerNative(any(), any(), any(), any(Pageable.class))).thenReturn(emptyPage);

        Page<CustomerDto> result = customerService.findByFlowerNative("НесуществующийЦветок", testDate, 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(customerRepository, times(1)).findByFlowerNative(any(), any(), any(), any(Pageable.class));
        verify(hashMap, times(1)).put(any(SearchKey.class), any(Page.class));
    }

    @Test
    void findByFlowerNative_DifferentPageAndSize() {
        Page<Customer> dbPage = new PageImpl<>(List.of(customer));

        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(false);
        when(customerRepository.findByFlowerNative(any(), any(), any(), any(Pageable.class))).thenReturn(dbPage);

        Page<CustomerDto> result = customerService.findByFlowerNative("Лилия", testDate, 3, 15);

        assertNotNull(result);
        verify(customerRepository, times(1)).findByFlowerNative(any(), any(), any(), any(Pageable.class));
    }

    @Test
    void findByFlowerNative_NullParameters() {
        Page<Customer> dbPage = new PageImpl<>(List.of(customer));

        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(false);
        when(customerRepository.findByFlowerNative(any(), any(), any(), any(Pageable.class))).thenReturn(dbPage);

        Page<CustomerDto> result = customerService.findByFlowerNative(null, null, 0, 10);

        assertNotNull(result);
        verify(customerRepository, times(1)).findByFlowerNative(any(), any(), any(), any(Pageable.class));
    }

    @Test
    void findByFlower_CacheWithDifferentKey_ShouldUseRepository() {
        Page<Customer> dbPage = new PageImpl<>(List.of(customer));

        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(false);
        when(customerRepository.findByFlower(any(), any(), any(), any(Pageable.class))).thenReturn(dbPage);

        Page<CustomerDto> result1 = customerService.findByFlower("Роза", testDate, 0, 10);
        Page<CustomerDto> result2 = customerService.findByFlower("Тюльпан", testDate, 0, 10);

        assertNotNull(result1);
        assertNotNull(result2);
        verify(customerRepository, times(2)).findByFlower(any(), any(), any(), any(Pageable.class));
        verify(hashMap, times(2)).put(any(SearchKey.class), any(Page.class));
    }

    @Test
    void findByFlower_CacheHitAfterFirstCall() {
        Page<Customer> dbPage = new PageImpl<>(List.of(customer));
        Page<CustomerDto> cachedPage = new PageImpl<>(List.of(customerDto));

        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(false, true);
        when(customerRepository.findByFlower(any(), any(), any(), any(Pageable.class))).thenReturn(dbPage);
        when(hashMap.get(any(SearchKey.class))).thenReturn(cachedPage);
        doNothing().when(hashMap).put(any(SearchKey.class), any(Page.class));

        Page<CustomerDto> result1 = customerService.findByFlower("Роза", testDate, 0, 10);
        Page<CustomerDto> result2 = customerService.findByFlower("Роза", testDate, 0, 10);

        assertNotNull(result1);
        assertNotNull(result2);
        verify(customerRepository, times(1)).findByFlower(any(), any(), any(), any(Pageable.class));
        verify(hashMap, times(1)).put(any(SearchKey.class), any(Page.class));
        verify(hashMap, times(2)).containsKey(any(SearchKey.class));
    }

    @Test
    void findByFlowerNative_CacheWithDifferentKey_ShouldUseRepository() {
        Page<Customer> dbPage = new PageImpl<>(List.of(customer));

        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(false);
        when(customerRepository.findByFlowerNative(any(), any(), any(), any(Pageable.class))).thenReturn(dbPage);

        Page<CustomerDto> result1 = customerService.findByFlowerNative("Лилия", testDate, 0, 10);
        Page<CustomerDto> result2 = customerService.findByFlowerNative("Орхидея", testDate, 0, 10);

        assertNotNull(result1);
        assertNotNull(result2);
        verify(customerRepository, times(2)).findByFlowerNative(any(), any(), any(), any(Pageable.class));
        verify(hashMap, times(2)).put(any(SearchKey.class), any(Page.class));
    }

    @Test
    void findByFlowerNative_CacheHitAfterFirstCall() {
        Page<Customer> dbPage = new PageImpl<>(List.of(customer));
        Page<CustomerDto> cachedPage = new PageImpl<>(List.of(customerDto));

        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(false, true);
        when(customerRepository.findByFlowerNative(any(), any(), any(), any(Pageable.class))).thenReturn(dbPage);
        when(hashMap.get(any(SearchKey.class))).thenReturn(cachedPage);
        doNothing().when(hashMap).put(any(SearchKey.class), any(Page.class));

        Page<CustomerDto> result1 = customerService.findByFlowerNative("Лилия", testDate, 0, 10);
        Page<CustomerDto> result2 = customerService.findByFlowerNative("Лилия", testDate, 0, 10);

        assertNotNull(result1);
        assertNotNull(result2);
        verify(customerRepository, times(1)).findByFlowerNative(any(), any(), any(), any(Pageable.class));
        verify(hashMap, times(1)).put(any(SearchKey.class), any(Page.class));
        verify(hashMap, times(2)).containsKey(any(SearchKey.class));
    }

    @Test
    void findByFlower_CacheWithDifferentDate_ShouldNotUseCache() {
        Page<Customer> dbPage = new PageImpl<>(List.of(customer));
        LocalDate date1 = LocalDate.of(2024, 1, 1);
        LocalDate date2 = LocalDate.of(2024, 1, 2);

        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(false);
        when(customerRepository.findByFlower(any(), any(), any(), any(Pageable.class))).thenReturn(dbPage);

        Page<CustomerDto> result1 = customerService.findByFlower("Роза", date1, 0, 10);
        Page<CustomerDto> result2 = customerService.findByFlower("Роза", date2, 0, 10);

        assertNotNull(result1);
        assertNotNull(result2);
        verify(customerRepository, times(2)).findByFlower(any(), any(), any(), any(Pageable.class));
    }

    @Test
    void findByFlowerNative_CacheWithDifferentPageSize_ShouldNotUseCache() {
        Page<Customer> dbPage = new PageImpl<>(List.of(customer));

        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(false);
        when(customerRepository.findByFlowerNative(any(), any(), any(), any(Pageable.class))).thenReturn(dbPage);

        Page<CustomerDto> result1 = customerService.findByFlowerNative("Лилия", testDate, 0, 10);
        Page<CustomerDto> result2 = customerService.findByFlowerNative("Лилия", testDate, 0, 20);

        assertNotNull(result1);
        assertNotNull(result2);
        verify(customerRepository, times(2)).findByFlowerNative(any(), any(), any(), any(Pageable.class));
    }

    @Test
    void findByFlower_CacheClearAfterUpdate_ShouldUseRepository() {
        Page<Customer> dbPage1 = new PageImpl<>(List.of(customer));
        Page<Customer> dbPage2 = new PageImpl<>(List.of(customer));
        Page<CustomerDto> cachedPage = new PageImpl<>(List.of(customerDto));

        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(false, true, false);
        when(customerRepository.findByFlower(any(), any(), any(), any(Pageable.class))).thenReturn(dbPage1, dbPage2);
        when(hashMap.get(any(SearchKey.class))).thenReturn(cachedPage);

        Page<CustomerDto> result1 = customerService.findByFlower("Роза", testDate, 0, 10);

        customerService.update(1L, customerDto);

        Page<CustomerDto> result2 = customerService.findByFlower("Роза", testDate, 0, 10);

        assertNotNull(result1);
        assertNotNull(result2);
        verify(customerRepository, times(2)).findByFlower(any(), any(), any(), any(Pageable.class));
        verify(hashMap, times(2)).clear();
    }

    @Test
    void findByFlowerNative_CacheClearAfterDelete_ShouldUseRepository() {
        Page<Customer> dbPage = new PageImpl<>(List.of(customer));

        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(false, false);
        when(customerRepository.findByFlowerNative(any(), any(), any(), any(Pageable.class))).thenReturn(dbPage);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).delete(customer);

        Page<CustomerDto> result1 = customerService.findByFlowerNative("Лилия", testDate, 0, 10);

        customerService.delete(1L);

        Page<CustomerDto> result2 = customerService.findByFlowerNative("Лилия", testDate, 0, 10);

        assertNotNull(result1);
        assertNotNull(result2);
        verify(customerRepository, times(2)).findByFlowerNative(any(), any(), any(), any(Pageable.class));
        verify(hashMap, times(2)).clear();
    }
}

