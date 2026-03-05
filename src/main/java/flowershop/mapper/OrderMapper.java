package flowershop.mapper;

import flowershop.dto.OrderDto;
import flowershop.entity.Order;
import flowershop.enums.OrderStatus;

import java.util.ArrayList;

public class OrderMapper {

    private OrderMapper() {

    }

    public static OrderDto toDto(Order order) {
        if (order == null) {
            return null;
        }

        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setDate(order.getDate());
        dto.setFinalPrice(order.getFinalPrice());
        if (order.getStatus() != null) {

            dto.setStatus(order.getStatus().name());
        }

        if (order.getCustomer() != null) {
            dto.setCustomerId(order.getCustomer().getId());
        }

        if (order.getBouquets() != null) {
            dto.setBouquets(order.getBouquets().stream()
                    .map(BouquetMapper::toDto)
                    .toList());
        }

        return dto;
    }

    public static Order toEntity(OrderDto dto) {
        if (dto == null) {
            return null;
        }

        Order order = new Order();
        order.setId(dto.getId());
        order.setDate(dto.getDate());
        order.setFinalPrice(dto.getFinalPrice());
        order.setStatus(OrderStatus.fromString(dto.getStatus()));
        order.setBouquets(new ArrayList<>());

        return order;
    }
}
