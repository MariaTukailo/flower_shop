package flowershop.service;

import flowershop.components.CustomerHashMap;
import flowershop.dto.FlowerDto;
import flowershop.entity.Bouquet;
import flowershop.entity.Flower;
import flowershop.enums.Color;
import flowershop.exception.TransactionDemoException;
import flowershop.repository.BouquetRepository;
import flowershop.repository.FlowerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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

    @Mock
    private FlowerRepository flowerRepository;
    @Mock
    private BouquetRepository bouquetRepository;
    @Mock
    private CustomerHashMap hashMap;

    @InjectMocks
    private FlowerService flowerService;

    private Flower flower;
    private FlowerDto flowerDto;

    @BeforeEach
    void setUp() {
        flower = new Flower();
        flower.setId(1L);
        flower.setName("Роза");
        flower.setPrice(10.0);
        flower.setActive(true);
        flower.setColor(Color.RED);

        flowerDto = new FlowerDto();
        flowerDto.setId(1L);
        flowerDto.setName("Роза");
        flowerDto.setPrice(10.0);
        flowerDto.setActive(true);
        flowerDto.setColor("RED");
    }

    @Test
    void saveAll_Success() {
        when(flowerRepository.saveAll(any())).thenReturn(List.of(flower));
        List<FlowerDto> result = flowerService.saveAll(List.of(flowerDto));
        assertEquals(1, result.size());
    }

    @Test
    void saveAllNotTransactional_ThrowsException() {
        assertThrows(TransactionDemoException.class, () -> flowerService.saveAllNotTransactional(List.of(flowerDto)));
    }

    @Test
    void saveAllTransactional_ThrowsException() {
        assertThrows(TransactionDemoException.class, () -> flowerService.saveAllTransactional(List.of(flowerDto)));
    }

    @Test
    void findAll_Success() {
        when(flowerRepository.findAll()).thenReturn(List.of(flower));
        List<FlowerDto> result = flowerService.findAll();
        assertFalse(result.isEmpty());
    }

    @Test
    void findById_Success() {
        when(flowerRepository.findById(1L)).thenReturn(Optional.of(flower));
        FlowerDto result = flowerService.findById(1L);
        assertNotNull(result);
    }

    @Test
    void findById_NotFound() {
        when(flowerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> flowerService.findById(1L));
    }

    @Test
    void findAllActive_Success() {
        Flower inactive = new Flower();
        inactive.setActive(false);
        when(flowerRepository.findAll()).thenReturn(List.of(flower, inactive));

        List<FlowerDto> result = flowerService.findAllActive();
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
    }

    @Test
    void create_Success() {
        when(flowerRepository.save(any())).thenReturn(flower);
        FlowerDto result = flowerService.create(flowerDto);
        assertNotNull(result);
        verify(flowerRepository).save(any());
    }

    @Test
    void update_PriceChanged_UpdatesBouquets() {
        flowerDto.setPrice(15.0); // Новая цена

        Bouquet bouquet = new Bouquet();
        bouquet.setPrice(100.0);
        bouquet.setFlowers(new ArrayList<>(List.of(flower)));

        when(flowerRepository.findById(1L)).thenReturn(Optional.of(flower));
        when(bouquetRepository.findAllWithFlowers()).thenReturn(List.of(bouquet));
        when(flowerRepository.save(any())).thenReturn(flower);

        FlowerDto result = flowerService.update(1L, flowerDto);

        assertEquals(15.0, result.getPrice());
        assertEquals(105.0, bouquet.getPrice()); // 100 - 10 + 15
        verify(hashMap).clear();
    }

    @Test
    void update_PriceNotChanged() {
        when(flowerRepository.findById(1L)).thenReturn(Optional.of(flower));
        when(flowerRepository.save(any())).thenReturn(flower);

        flowerService.update(1L, flowerDto);

        verify(bouquetRepository, never()).findAllWithFlowers();
    }

    @Test
    void updateStatus_SetInactive_UpdatesBouquets() {
        Bouquet bouquet = new Bouquet();
        bouquet.setActive(true);
        bouquet.setFlowers(new ArrayList<>(List.of(flower)));

        when(flowerRepository.findById(1L)).thenReturn(Optional.of(flower));
        when(bouquetRepository.findAllWithFlowers()).thenReturn(List.of(bouquet));
        when(flowerRepository.save(any())).thenReturn(flower);

        flowerService.updateStatus(1L, false);

        assertFalse(bouquet.isActive());
    }

    @Test
    void updateStatus_SetActive_NoBouquetUpdate() {
        flower.setActive(false);
        when(flowerRepository.findById(1L)).thenReturn(Optional.of(flower));
        when(flowerRepository.save(any())).thenReturn(flower);

        flowerService.updateStatus(1L, true);

        verify(bouquetRepository, never()).findAllWithFlowers();
    }
}