package flowershop.controller;

import flowershop.dto.FlowerDto;
import flowershop.service.FlowerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Управление цветами", description = "Методы для работы с ассортиментом магазина")
@Validated
@RestController
@RequestMapping("/flowers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class FlowerController {

    private final FlowerService flowerService;


    @Operation(summary = "Получить все цветы ", description = "Возвращает список всех цветов ")
    @GetMapping
    public List<FlowerDto> findAll() {
        return flowerService.findAll();
    }

    @Operation(summary = "Получить все активные цветы ", description = "Возвращает список всех активных цветов ")
    @GetMapping("/active")
    public List<FlowerDto> findAllActive() {
        return flowerService.findAllActive();
    }


    @Operation(summary = "Найти цветы по ID ", description = "Возвращает найденные цветы по ID ")
    @GetMapping("/{id}")
    public FlowerDto findById(@PathVariable Long id) {
        return flowerService.findById(id);
    }

    @Operation(summary = "Создать цветы (bulk)", description = "Создает цветы (bulk)")
    @PostMapping("/bulk")
    public List<FlowerDto> addBulk(@Valid @RequestBody List<FlowerDto> flowers) {
        return flowerService.saveAll(flowers);
    }

    @Operation(summary = "Создать цветы (bulk без Transactional)", description = "Создает цветы (bulk)")
    @PostMapping("/bulkNotTransactional")
    public List<FlowerDto> addBulkNotTransactional(@Valid @RequestBody List<FlowerDto> flowers) {
        return flowerService.saveAllNotTransactional(flowers);
    }

    @Operation(summary = "Создать цветы (bulk  Transactional)", description = "Создает цветы (bulk)")
    @PostMapping("/bulkTransactional")
    public List<FlowerDto> addBulkTransactional(@Valid @RequestBody List<FlowerDto> flowers) {
        return flowerService.saveAllTransactional(flowers);
    }

    @Operation(summary = "Создать цветы ", description = "Создает цветы ")
    @PostMapping
    public FlowerDto create(@Valid @RequestBody FlowerDto dto) {
        return flowerService.create(dto);
    }

    @Operation(summary = "Изменить определенные цветы ", description = "Изменяет определенные цветы ")
    @PutMapping("/{id}")
    public FlowerDto update(@PathVariable Long id, @Valid @RequestBody FlowerDto dto) {
        return flowerService.update(id, dto);
    }

    @Operation(summary = "Изменить статус цветов ", description = "Меняет статус цветов (активный/неактивный) ")
    @PatchMapping("/{id}/status")
    public FlowerDto updateStatus(@PathVariable Long id,

                                  @RequestParam
                                  @NotNull(message = "Укажите, активен ли цветок (true/false)")
                                  boolean active) {

        return flowerService.updateStatus(id, active);
    }
}
