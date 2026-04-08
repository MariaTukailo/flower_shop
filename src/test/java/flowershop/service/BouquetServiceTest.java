package flowershop.service;

import flowershop.dto.BouquetDto;
import flowershop.dto.FlowerDto;
import flowershop.entity.Bouquet;
import flowershop.entity.Flower;
import flowershop.repository.BouquetRepository;
import flowershop.repository.FlowerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BouquetServiceTest {

    @Mock
    private BouquetRepository bouquetRepository;
    @Mock
    private FlowerRepository flowerRepository;

    @InjectMocks
    private BouquetService bouquetService;

    private Bouquet bouquet;
    private Flower flower;
    private Flower inactiveFlower;

    @BeforeEach
    void setUp() {
        flower = new Flower();
        flower.setId(1L);
        flower.setActive(true);
        flower.setPrice(10.0);

        inactiveFlower = new Flower();
        inactiveFlower.setId(2L);
        inactiveFlower.setActive(false);
        inactiveFlower.setPrice(5.0);

        bouquet = new Bouquet();
        bouquet.setId(1L);
        bouquet.setName("Тест");
        bouquet.setActive(true);
        bouquet.setPrice(100.0);
        bouquet.setFlowers(new ArrayList<>(List.of(flower)));
    }

    // --- ТЕСТЫ НА ПОИСК (READ) ---

    @Test
    void findAll_Success() {
        when(bouquetRepository.findAll()).thenReturn(List.of(bouquet));
        List<BouquetDto> result = bouquetService.findAll();
        assertEquals(1, result.size());
        verify(bouquetRepository).findAll();
    }

    @Test
    void findAllActive_Success() {
        Bouquet inactiveB = new Bouquet();
        inactiveB.setActive(false);
        when(bouquetRepository.findAll()).thenReturn(List.of(bouquet, inactiveB));

        List<BouquetDto> result = bouquetService.findAllActive();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
    }

    @Test
    void findById_Success() {
        when(bouquetRepository.findById(1L)).thenReturn(Optional.of(bouquet));
        BouquetDto result = bouquetService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findById_NotFound_ThrowsException() {
        when(bouquetRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> bouquetService.findById(99L));
    }

    @Test
    void findAllOptimized_Success() {
        when(bouquetRepository.findAllWithFlowers()).thenReturn(List.of(bouquet));
        List<BouquetDto> result = bouquetService.findAllOptimized();
        assertNotNull(result);
        verify(bouquetRepository).findAllWithFlowers();
    }

    // --- ТЕСТЫ НА СОЗДАНИЕ (CREATE) ---

    @Test
    void create_Success_FiltersInactiveFlowers() {
        FlowerDto f1 = new FlowerDto(); f1.setId(1L);
        FlowerDto f2 = new FlowerDto(); f2.setId(2L);

        BouquetDto inputDto = new BouquetDto();
        inputDto.setName("Микс");
        inputDto.setPrice(200.0);
        inputDto.setFlowers(List.of(f1, f2));

        when(flowerRepository.findAllById(any())).thenReturn(List.of(flower, inactiveFlower));
        when(bouquetRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        BouquetDto result = bouquetService.create(inputDto);

        assertEquals(200.0, result.getPrice()); // Цена из DTO
        assertEquals(1, result.getFlowers().size()); // Только активный цветок
        assertTrue(result.isActive());
    }

    // --- ТЕСТЫ НА СТАТУС (UPDATE STATUS) ---

    @Test
    void updateStatus_ToActive_Success() {
        bouquet.setActive(false);
        when(bouquetRepository.findById(1L)).thenReturn(Optional.of(bouquet));
        when(bouquetRepository.save(any())).thenReturn(bouquet);

        BouquetDto result = bouquetService.updateStatus(1L, true);

        assertTrue(result.isActive());
        verify(bouquetRepository).save(any());
    }

    @Test
    void updateStatus_ToActive_Failure_InactiveFlowers() {
        // 1. Создаем неактивный цветок
        Flower inactiveF = new Flower();
        inactiveF.setId(10L);
        inactiveF.setActive(false);

        // 2. Кладем его в букет и ставим статус букета false
        bouquet.setActive(false);
        bouquet.setFlowers(new ArrayList<>(List.of(inactiveF)));

        // 3. Настраиваем мок на поиск этого букета
        when(bouquetRepository.findById(1L)).thenReturn(Optional.of(bouquet));

        // 4. Вызываем метод (пытаемся активировать букет)
        BouquetDto result = bouquetService.updateStatus(1L, true);

        // 5. ПРОВЕРКИ
        // Букет НЕ должен стать активным, так как внутри есть неактивный цветок
        assertFalse(result.isActive());
        // Метод save НЕ должен был вызваться (согласно твоей логике в сервисе)
        verify(bouquetRepository, never()).save(any());
    }

    @Test
    void updateStatus_ToInactive_Success() {
        when(bouquetRepository.findById(1L)).thenReturn(Optional.of(bouquet));
        when(bouquetRepository.save(any())).thenReturn(bouquet);

        BouquetDto result = bouquetService.updateStatus(1L, false);

        assertFalse(result.isActive());
        verify(bouquetRepository).save(any());
    }

    @Test
    void updateStatus_NotFound_ThrowsException() {
        when(bouquetRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> bouquetService.updateStatus(99L, true));
    }

    // --- ТЕСТЫ НА ЧАСТИЧНОЕ ОБНОВЛЕНИЕ (PATCH) ---

    @Test
    void updatePartial_UpdateAllFields() {
        when(bouquetRepository.findById(1L)).thenReturn(Optional.of(bouquet));
        when(bouquetRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        BouquetDto result = bouquetService.updatePartial(1L, false, 55.5, "new_photo.jpg");

        assertFalse(result.isActive());
        assertEquals(55.5, result.getPrice());
        assertEquals("new_photo.jpg", result.getPathPhoto());
    }

    @Test
    void updatePartial_UpdateOnlyPrice() {
        when(bouquetRepository.findById(1L)).thenReturn(Optional.of(bouquet));
        when(bouquetRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        BouquetDto result = bouquetService.updatePartial(1L, null, 300.0, null);

        assertEquals(300.0, result.getPrice());
        assertTrue(result.isActive()); // Остался прежним
    }

    @Test
    void updatePartial_BlankPhoto_DoesNotUpdate() {
        bouquet.setPathPhoto("old.jpg");
        when(bouquetRepository.findById(1L)).thenReturn(Optional.of(bouquet));
        when(bouquetRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        bouquetService.updatePartial(1L, null, null, "   ");

        assertEquals("old.jpg", bouquet.getPathPhoto());
    }

    @Test
    void updatePartial_NotFound() {
        when(bouquetRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                bouquetService.updatePartial(99L, true, 1.0, ""));
    }
}