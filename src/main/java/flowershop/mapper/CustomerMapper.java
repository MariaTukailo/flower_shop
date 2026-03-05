package flowershop.mapper;

import flowershop.dto.CustomerDto;
import flowershop.entity.Customer;
import flowershop.entity.Order;
import java.util.ArrayList;

public class CustomerMapper {

    private CustomerMapper() { }

    public static CustomerDto toDto(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setPhoneNumber(customer.getPhoneNumber());


        if (customer.getCart() != null) {
            dto.setShoppingCartId(customer.getCart().getId());
        }

        if (customer.getOrders() != null) {
            dto.setOrderIds(customer.getOrders().stream()
                    .map(Order::getId)
                    .toList());
        }

        return dto;
    }

    public static Customer toEntity(CustomerDto dto) {
        if (dto == null) {
            return null;
        }

        Customer customer = new Customer();
        customer.setId(dto.getId());
        customer.setName(dto.getName());
        customer.setPhoneNumber(dto.getPhoneNumber());

        customer.setOrders(new ArrayList<>());

        return customer;
    }
}
