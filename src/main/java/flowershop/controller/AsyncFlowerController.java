package flowershop.controller;

import flowershop.dto.AsyncTaskResponse;
import flowershop.dto.FlowerDto;
import flowershop.enums.TaskStatus;
import flowershop.asyncservice.AsyncFlowerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


import static flowershop.enums.TaskStatus.ACCEPTED;

@Tag(name = "Управление цветами(асинхронные операции)", description = "Методы для работы с ассортиментом магазина (асинхронные операции)")
@RestController
@RequestMapping("/flowers/async")
@RequiredArgsConstructor
public class AsyncFlowerController {

    private final AsyncFlowerService asyncFlowerService;

    @Operation(summary = "Создать цветы Async", description = "Создает цветы (bulk)")
    @PostMapping
    public AsyncTaskResponse addAsync(@Valid @RequestBody List<FlowerDto> flowers) {

        long id = asyncFlowerService.createNewTask();
        asyncFlowerService.processFlowersAsync(id,flowers);
        return new AsyncTaskResponse(id,ACCEPTED);
    }

    @Operation(summary = "Получить статус задачи", description = "Возвращает текущий статус обработки цветов по ID задачи")
    @GetMapping("/status/{id}")
    public AsyncTaskResponse getStatus(@PathVariable Long id) {
        TaskStatus status = asyncFlowerService.getTaskStatus(id);
        return new AsyncTaskResponse(id,status);
    }

    @Operation(summary = "Продемонстрировать проблему Race Condition", description = "Демонстрация ошибки  Race Condition при 60 потоках")
    @GetMapping("/test_problem")
    public String testRace() throws InterruptedException {
        return asyncFlowerService.runRaceConditionTest();
    }

    @Operation(summary = "Продемонстрировать решение Race Condition", description = "Демонстрация корректной работы с AtomicLong")
    @GetMapping("/test_solution")
    public String testAtomic() throws InterruptedException {
        return asyncFlowerService.runAtomicSolutionTest();
    }

}
