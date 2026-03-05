package flowershop.controller;

import flowershop.dto.OrderDto;
import flowershop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @GetMapping
    public List<OrderDto> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public OrderDto findById(@PathVariable Long id) {
        OrderDto orderDto = orderService.findById(id);
        if (orderDto == null) {
            return null;
        }
        return orderDto;
    }


    @PostMapping("/checkout/{customerId}")
    public OrderDto createFromCart(@PathVariable Long customerId) {
        OrderDto orderDto = orderService.createFromCart(customerId);
        if (orderDto == null) {

            return null;
        }
        return orderDto;
    }


    @PatchMapping("/{id}/status")
    public OrderDto updateStatus(@PathVariable Long id, @RequestParam String status) {
        return orderService.updateStatus(id, status);
    }
}