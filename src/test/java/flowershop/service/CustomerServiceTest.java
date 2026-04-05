package flowershop.service;

import flowershop.components.CustomerHashMap;
import flowershop.dto.CustomerDto;
import flowershop.entity.Customer;
import flowershop.entity.SearchKey;
import flowershop.exception.TransactionDemoException;
import flowershop.repository.CustomerRepository;
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
    private CustomerHashMap hashMap;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Иван");
        customer.setPhoneNumber("12345");

        customerDto = new CustomerDto();
        customerDto.setId(1L);
        customerDto.setName("Иван");
        customerDto.setPhoneNumber("12345");
    }

    @Test
    void findAll_Success() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));
        List<CustomerDto> result = customerService.findAll();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findById_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        CustomerDto result = customerService.findById(1L);
        assertNotNull(result);
        assertEquals("Иван", result.getName());
    }

    @Test
    void findById_NotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> customerService.findById(1L));
    }

 

    @Test
    void createWithoutTransaction_ThrowsException() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        assertThrows(TransactionDemoException.class, () -> customerService.createWithoutTransaction(customerDto));
    }

    @Test
    void createWithTransaction_ThrowsException() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        assertThrows(TransactionDemoException.class, () -> customerService.createWithTransaction(customerDto));
    }

    @Test
    void update_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        CustomerDto result = customerService.update(1L, customerDto);
        assertNotNull(result);
        verify(hashMap).clear();
    }

    @Test
    void delete_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        customerService.delete(1L);
        verify(customerRepository).delete(customer);
        verify(hashMap).clear();
    }

    @Test
    void findByFlower_FromCache() {
        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(true);
        Page<CustomerDto> cachedPage = new PageImpl<>(List.of(customerDto));
        when(hashMap.get(any(SearchKey.class))).thenReturn(cachedPage);

        Page<CustomerDto> result = customerService.findByFlower("Роза", LocalDate.now(), 0, 10);

        assertEquals(1, result.getTotalElements());
        verify(customerRepository, never()).findByFlower(any(), any(), any(), any());
    }

    @Test
    void findByFlower_FromRepository() {
        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(false);
        Page<Customer> dbPage = new PageImpl<>(List.of(customer));
        when(customerRepository.findByFlower(any(), any(), any(), any(Pageable.class))).thenReturn(dbPage);

        Page<CustomerDto> result = customerService.findByFlower("Роза", LocalDate.now(), 0, 10);

        assertEquals(1, result.getTotalElements());
        verify(hashMap).put(any(), any());
    }

    @Test
    void findByFlowerNative_FromCache() {
        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(true);
        Page<CustomerDto> cachedPage = new PageImpl<>(List.of(customerDto));
        when(hashMap.get(any(SearchKey.class))).thenReturn(cachedPage);

        Page<CustomerDto> result = customerService.findByFlowerNative("Лилия", LocalDate.now(), 0, 10);

        assertEquals(1, result.getTotalElements());
        verify(customerRepository, never()).findByFlowerNative(any(), any(), any(), any());
    }

    @Test
    void findByFlowerNative_FromRepository() {
        when(hashMap.containsKey(any(SearchKey.class))).thenReturn(false);
        Page<Customer> dbPage = new PageImpl<>(List.of(customer));
        when(customerRepository.findByFlowerNative(any(), any(), any(), any(Pageable.class))).thenReturn(dbPage);

        Page<CustomerDto> result = customerService.findByFlowerNative("Лилия", LocalDate.now(), 0, 10);

        assertEquals(1, result.getTotalElements());
        verify(hashMap).put(any(), any());
    }
}