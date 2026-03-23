package flowershop.controller;

import flowershop.dto.CustomerDto;
import flowershop.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Управление покупателями", description = "Методы для работы с покупателями магазина")
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Получить всех покупателей ", description = "Возвращает список всех покупателей ")
    @GetMapping
    public List<CustomerDto> findAll() {
        return customerService.findAll();
    }

    @Operation(summary = "Найти покупателя по ID ", description = "Возвращает найденного покупателя ")
    @GetMapping("/{id}")
    public CustomerDto findById(@PathVariable Long id) {
        return customerService.findById(id);
    }

    @Operation(summary = "Создать покупателя", description = "Создает покупателя ")
    @PostMapping
    public CustomerDto create(@Valid @RequestBody CustomerDto dto) {

        return customerService.createTransactional(dto);
    }

    @Operation(summary = "Изменить покупателя ", description = "Изменяет параметры определенного покупателя ")
    @PutMapping("/{id}")
    public CustomerDto update(@PathVariable Long id, @Valid @RequestBody CustomerDto dto) {
        return customerService.update(id, dto);
    }

    @Operation(summary = "Удалить покупателя", description = "Удаляет определенного покупателя ")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        customerService.delete(id);
    }

    @Operation(summary = "Получить всех покупателей с активными заказами, в которых содержится определенный цветок (JPQL)",
            description = "Возвращает список найденных покупателей ")
    @GetMapping("/find-by-flowers")
    public Page<CustomerDto> findByFlowers(@RequestParam String flowerName,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "1") int size) {
        return customerService.findByFlower(flowerName, date, page, size);
    }

    @Operation(summary = "Получить всех покупателей с активными заказами, в которых содержится определенный цветок (native)",
              description = "Возвращает список найденных покупателей ")
    @GetMapping("/find-by-flowers-native")
    public Page<CustomerDto> findByFlowersNative(@RequestParam String flowerName,
                                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "1") int size) {
        return customerService.findByFlowerNative(flowerName, date, page, size);
    }
}