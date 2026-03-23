package flowershop.service;

import flowershop.components.CustomerHashMap;
import flowershop.dto.FlowerDto;
import flowershop.entity.Bouquet;
import flowershop.entity.Flower;
import flowershop.exception.TransactionDemoException;
import flowershop.repository.BouquetRepository;
import flowershop.repository.FlowerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlowerServiceTest {

    @Mock private FlowerRepository flowerRepository;
    @Mock private BouquetRepository bouquetRepository;
    @Mock private CustomerHashMap hashMap;

    @InjectMocks private FlowerService flowerService;

    @Test
    @DisplayName("findAll: Успешное получение списка всех цветов")
    void findAll_Success() {
        Flower flower = new Flower();
        flower.setName("Роза");
        when(flowerRepository.findAll()).thenReturn(List.of(flower));

        List<FlowerDto> result = flowerService.findAll();

        assertEquals(1, result.size());
        assertEquals("Роза", result.get(0).getName());
    }

    @Test
    @DisplayName("findById: Успешный поиск")
    void findById_Success() {
        Flower flower = new Flower();
        flower.setId(1L);
        when(flowerRepository.findById(1L)).thenReturn(Optional.of(flower));

        FlowerDto result = flowerService.findById(1L);

        assertNotNull(result);
        verify(flowerRepository).findById(1L);
    }

    @Test
    @DisplayName("findById: Ошибка EntityNotFoundException (Закрывает orElseThrow)")
    void findById_NotFound() {
        when(flowerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> flowerService.findById(1L));
    }

    @Test
    @DisplayName("findAllActive: Проверка фильтрации только активных цветов")
    void findAllActive_FilterCheck() {
        Flower active = new Flower(); active.setActive(true);
        Flower inactive = new Flower(); inactive.setActive(false);
        when(flowerRepository.findAll()).thenReturn(List.of(active, inactive));

        List<FlowerDto> result = flowerService.findAllActive();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("saveAll: Успешный bulk save")
    void saveAll_Success() {
        FlowerDto dto = new FlowerDto();
        dto.setName("Лилия");
        when(flowerRepository.saveAll(anyList())).thenReturn(List.of(new Flower()));

        var result = flowerService.saveAll(List.of(dto));

        assertNotNull(result);
        verify(flowerRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("saveAllTransactional: Проверка выброса спец-исключения")
    void saveAllTransactional_ThrowsException() {
        assertThrows(TransactionDemoException.class, () -> flowerService.saveAllTransactional(List.of(new FlowerDto())));
    }

    @Test
    @DisplayName("saveAllNotTransactional: Проверка выброса спец-исключения")
    void saveAllNotTransactional_ThrowsException() {
        assertThrows(TransactionDemoException.class, () -> flowerService.saveAllNotTransactional(List.of(new FlowerDto())));
    }

    @Test
    @DisplayName("update: Изменение цены и обновление букетов (Закрывает логику пересчета)")
    void update_PriceChange_UpdatesBouquets() {
        Long flowerId = 1L;
        Flower flower = new Flower();
        flower.setId(flowerId);
        flower.setPrice(10.0);
        flower.setColor(flowershop.enums.Color.RED);

        FlowerDto updateDto = new FlowerDto();
        updateDto.setPrice(15.0);
        updateDto.setColor("красный");

        Bouquet bouquet = new Bouquet();
        bouquet.setPrice(50.0);
        bouquet.setFlowers(new ArrayList<>(List.of(flower)));

        when(flowerRepository.findById(flowerId)).thenReturn(Optional.of(flower));
        when(bouquetRepository.findAllWithFlowers()).thenReturn(List.of(bouquet));
        when(flowerRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        flowerService.update(flowerId, updateDto);


        assertEquals(55.0, bouquet.getPrice());
        verify(hashMap).clear(); // Проверяем очистку кэша
    }

    @Test
    @DisplayName("updateStatus: Деактивация цветка отключает букеты (Закрывает if(!active))")
    void updateStatus_Deactivate_DisablesBouquets() {
        Long flowerId = 1L;
        Flower flower = new Flower();
        flower.setActive(true);

        Bouquet bouquet = new Bouquet();
        bouquet.setActive(true);
        bouquet.setFlowers(new ArrayList<>(List.of(flower)));

        when(flowerRepository.findById(flowerId)).thenReturn(Optional.of(flower));
        when(bouquetRepository.findAllWithFlowers()).thenReturn(List.of(bouquet));
        when(flowerRepository.save(any())).thenReturn(flower);

        flowerService.updateStatus(flowerId, false);

        assertFalse(bouquet.isActive());
    }

    @Test
    @DisplayName("create: Успешное создание")
    void create_Success() {
        FlowerDto dto = new FlowerDto();
        dto.setColor("красный");
        when(flowerRepository.save(any())).thenReturn(new Flower());

        flowerService.create(dto);

        verify(flowerRepository).save(any());
    }
}