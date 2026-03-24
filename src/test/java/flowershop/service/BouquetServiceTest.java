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

    @BeforeEach
    void setUp() {
        flower = new Flower();
        flower.setId(1L);
        flower.setPrice(50.0);
        flower.setActive(true);

        bouquet = new Bouquet();
        bouquet.setId(1L);
        bouquet.setName("Весенний");
        bouquet.setFlowers(new ArrayList<>(List.of(flower)));
        bouquet.setActive(true);
    }

    @Test
    void findAll_Success() {
        when(bouquetRepository.findAll()).thenReturn(List.of(bouquet));
        List<BouquetDto> result = bouquetService.findAll();
        assertFalse(result.isEmpty());
    }

    @Test
    void findAllActive_Success() {
        Bouquet inactive = new Bouquet();
        inactive.setActive(false);
        when(bouquetRepository.findAll()).thenReturn(List.of(bouquet, inactive));
        List<BouquetDto> result = bouquetService.findAllActive();
        assertEquals(1, result.size());
    }

    @Test
    void findById_Success() {
        when(bouquetRepository.findById(1L)).thenReturn(Optional.of(bouquet));
        BouquetDto result = bouquetService.findById(1L);
        assertNotNull(result);
    }

    @Test
    void findById_NotFound() {
        when(bouquetRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> bouquetService.findById(1L));
    }

    @Test
    void findAllOptimized_Success() {
        when(bouquetRepository.findAllWithFlowers()).thenReturn(List.of(bouquet));
        List<BouquetDto> result = bouquetService.findAllOptimized();
        assertFalse(result.isEmpty());
    }

    @Test
    void create_Success_FiltersInactiveFlowers() {
        Flower inactiveFlower = new Flower();
        inactiveFlower.setId(2L);
        inactiveFlower.setActive(false);
        inactiveFlower.setPrice(30.0);

        FlowerDto f1 = new FlowerDto(); f1.setId(1L);
        FlowerDto f2 = new FlowerDto(); f2.setId(2L);

        BouquetDto dto = new BouquetDto();
        dto.setName("Микс");
        dto.setFlowers(List.of(f1, f2));

        when(flowerRepository.findAllById(any())).thenReturn(List.of(flower, inactiveFlower));
        when(bouquetRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        BouquetDto result = bouquetService.create(dto);

        assertEquals(50.0, result.getPrice()); // Только активный цветок (50.0)
        verify(bouquetRepository).save(any());
    }

    @Test
    void updateStatus_SetActive_Failure_DueToInactiveFlowers() {
        flower.setActive(false); // В букете неактивный цветок
        when(bouquetRepository.findById(1L)).thenReturn(Optional.of(bouquet));

        bouquetService.updateStatus(1L, true);

        assertTrue(bouquet.isActive()); // Статус не изменился бы, если бы был false
        verify(bouquetRepository, never()).save(any());
    }

    @Test
    void updateStatus_SetInactive_Success() {
        when(bouquetRepository.findById(1L)).thenReturn(Optional.of(bouquet));
        when(bouquetRepository.save(any())).thenReturn(bouquet);

        bouquetService.updateStatus(1L, false);

        assertFalse(bouquet.isActive());
        verify(bouquetRepository).save(any());
    }

    @Test
    void updateStatus_NotFound() {
        when(bouquetRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> bouquetService.updateStatus(1L, true));
    }

    @Test
    void create_EmptyOrInactiveFlowers_PriceIsZero() {
        Flower inactiveFlower = new Flower();
        inactiveFlower.setId(99L);
        inactiveFlower.setActive(false);
        inactiveFlower.setPrice(100.0);

        FlowerDto f1 = new FlowerDto();
        f1.setId(99L);

        BouquetDto dto = new BouquetDto();
        dto.setName("Пустой букет");
        dto.setFlowers(List.of(f1));

        when(flowerRepository.findAllById(any())).thenReturn(List.of(inactiveFlower));
        when(bouquetRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        BouquetDto result = bouquetService.create(dto);

        assertEquals(0.0, result.getPrice());
        assertTrue(result.getFlowers().isEmpty());
    }

    @Test
    void updateStatus_SetActive_Success_AllFlowersActive() {
        bouquet.setActive(false);
        flower.setActive(true);

        when(bouquetRepository.findById(1L)).thenReturn(Optional.of(bouquet));
        when(bouquetRepository.save(any())).thenReturn(bouquet);

        BouquetDto result = bouquetService.updateStatus(1L, true);

        assertTrue(result.isActive());
        verify(bouquetRepository).save(bouquet);
    }

    @Test
    void findAllActive_FiltersCorrectl() {
        Bouquet b1 = new Bouquet();
        b1.setActive(true);
        Bouquet b2 = new Bouquet();
        b2.setActive(false);

        when(bouquetRepository.findAll()).thenReturn(List.of(b1, b2));

        List<BouquetDto> result = bouquetService.findAllActive();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
    }

    @Test
    void updateStatus_SetActive_Success_EmptyFlowers() {

        bouquet.setActive(false);
        bouquet.setFlowers(new ArrayList<>());

        when(bouquetRepository.findById(1L)).thenReturn(Optional.of(bouquet));
        when(bouquetRepository.save(any())).thenReturn(bouquet);

        BouquetDto result = bouquetService.updateStatus(1L, true);

        assertTrue(result.isActive());
        verify(bouquetRepository).save(bouquet);
    }

    @Test
    void updateStatus_SetInactive_AlreadyInactive() {

        bouquet.setActive(false);

        when(bouquetRepository.findById(1L)).thenReturn(Optional.of(bouquet));
        when(bouquetRepository.save(any())).thenReturn(bouquet);

        BouquetDto result = bouquetService.updateStatus(1L, false);

        assertFalse(result.isActive());
        verify(bouquetRepository).save(bouquet);
    }

    @Test
    void create_WithNullFlowersList() {

        BouquetDto dto = new BouquetDto();
        dto.setName("Null Flowers");
        dto.setFlowers(new ArrayList<>());

        when(flowerRepository.findAllById(any())).thenReturn(new ArrayList<>());
        when(bouquetRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        BouquetDto result = bouquetService.create(dto);

        assertEquals(0.0, result.getPrice());
        verify(bouquetRepository).save(any());
    }
}