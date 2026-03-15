package flowershop.controller;

import flowershop.dto.OrderDto;
import flowershop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Tag(name = "Управление заказами", description = "Методы для работы с заказами покупателей")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Получить все заказы ", description = "Возвращает список всех заказов ")
    @GetMapping
    public List<OrderDto> findAll() {
        return orderService.findAll();
    }

    @Operation(summary = "Получить заказ по ID ", description = "Возвращает заказ по ID ")
    @GetMapping("/{id}")
    public OrderDto findById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @Operation(summary = "Создать заказ ", description = "Создает заказ")
    @PostMapping("/checkout/{customerId}")
    public OrderDto createFromCart(
            @PathVariable Long customerId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @FutureOrPresent(message = "Дата доставки не может быть в прошлом")LocalDate deliveryDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalTime deliveryTime) {


        return orderService.createFromCart(customerId, deliveryDate, deliveryTime);
    }

    @Operation(summary = "Изменить статус заказа ", description = "Изменяет статус заказа ( в обработке, принят, в пути, доставлен, отменен ")
    @PatchMapping("/{id}/status")
    public OrderDto updateStatus(@PathVariable Long id,

                                 @RequestParam
                                 @NotNull(message = "Статус не указан или указан некорректно. Доступные статусы: Обработка, Принят, В пути, Отменен, Доставлен")
                                 String status) {

        return orderService.updateStatus(id, status);
    }
}