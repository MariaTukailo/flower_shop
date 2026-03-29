package flowershop.asyncservice;

import flowershop.dto.FlowerDto;
import flowershop.entity.Flower;
import flowershop.enums.TaskStatus;
import flowershop.mapper.FlowerMapper;
import flowershop.repository.FlowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class AsyncFlowerService {

    private final FlowerRepository flowerRepository;
    private final Map<Long, TaskStatus> taskStatusMap = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    private final AtomicLong testAtomicCounter = new AtomicLong(0);
    private long unsafeCounter = 0;

    public long createNewTask() {
        long taskId = idGenerator.getAndIncrement();
        taskStatusMap.put(taskId, TaskStatus.ACCEPTED);
        return taskId;
    }

    @Async
    public void processFlowersAsync(Long taskId, List<FlowerDto> flowersDto) {

        try {
            taskStatusMap.put(taskId, TaskStatus.IN_PROGRESS);
            List<Flower> flowers = flowersDto.stream()
                    .map(FlowerMapper::toEntity)
                    .toList();

            flowerRepository.saveAll(flowers);
            taskStatusMap.put(taskId, TaskStatus.SAVED);
            Thread.sleep(10000);
            taskStatusMap.put(taskId, TaskStatus.COMPLETED);

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();
            taskStatusMap.put(taskId, TaskStatus.FAILED);

        }
    }

    public TaskStatus getTaskStatus(Long id) {
        return taskStatusMap.get(id);
    }


    public String runRaceConditionTest() throws InterruptedException {
        unsafeCounter = 0;

        long total = 10000;

        ExecutorService testExecutor = Executors.newFixedThreadPool(60);

        for (int i = 0; i < total; i++) {
            testExecutor.submit(() -> unsafeCounter++);
        }

        testExecutor.shutdown();
        testExecutor.awaitTermination(5, TimeUnit.SECONDS);

        return "Демонстрация проблемы Race Condition:\n" +
                "Ожидание: " + total + "\n" +
                "Получили: " + unsafeCounter + "\n";
    }


    public String runAtomicSolutionTest() throws InterruptedException {

        long total = 10000;
        testAtomicCounter.set(0);

        ExecutorService testExecutor = Executors.newFixedThreadPool(60);

        for (int i = 0; i < total; i++) {
            testExecutor.submit(testAtomicCounter::getAndIncrement);
        }

        testExecutor.shutdown();
        testExecutor.awaitTermination(5, TimeUnit.SECONDS);

        return "Решение проблемы Race Condition:\n" +
                "Ожидание: " + total + "\n" +
                "Получили: " + testAtomicCounter.get() + "\n";
    }

}
