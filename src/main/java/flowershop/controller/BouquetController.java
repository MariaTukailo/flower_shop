package flowershop.controller;

import flowershop.dto.BouquetDto;
import flowershop.service.BouquetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Управление букетами", description = "Методы для работы с букетами")
@Validated
@RestController
@RequestMapping("/bouquets")
@RequiredArgsConstructor
public class BouquetController {

    private final BouquetService bouquetService;

    @Operation(summary = "Получить весь ассортимент букетов ", description = "Возвращает список всех букетов ")
    @GetMapping
    public List<BouquetDto> findAll() {
        return bouquetService.findAllOptimized();
    }

    @Operation(summary = "Получить весь ассортимент активных букетов ", description = "Возвращает список всех активных букетов ")
    @GetMapping("/active")
    public List<BouquetDto> findAllActive() {
        return bouquetService.findAllActive();
    }

    @Operation(summary = "Поиск букета по ID", description = "Возвращает найденный букет ")
    @GetMapping("/{id}")
    public BouquetDto findById(@PathVariable Long id) {
        return bouquetService.findById(id);
    }

    @Operation(summary = "Создание букета ", description = "Создает новый букет ")
    @PostMapping
    public BouquetDto create(@Valid @RequestBody BouquetDto dto) {
        return bouquetService.create(dto);
    }

    @Operation(summary = "Изменение статуса букета", description = "Изменяет статус букета (активный/неактивный) ")
    @PatchMapping("/{id}/status")
    public BouquetDto updateStatus(@PathVariable Long id,
                                   @RequestParam
                                   @NotNull(message = "Укажите, активен ли букет (true/false)")
                                   boolean active) {
        return bouquetService.updateStatus(id, active);
    }
}