package flowershop.service;

import flowershop.components.CustomerHashMap;
import flowershop.dto.FlowerDto;
import flowershop.enums.Color;
import flowershop.exception.TransactionDemoException;
import flowershop.mapper.FlowerMapper;
import flowershop.entity.Bouquet;
import flowershop.entity.Flower;
import flowershop.repository.FlowerRepository;
import flowershop.repository.BouquetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlowerService {

    private final FlowerRepository flowerRepository;
    private final BouquetRepository bouquetRepository;
    private final CustomerHashMap hashMap;

    private Flower findEntityById(Long id) {
        log.debug("Поиск цветка по ID {}", id);
        return flowerRepository.findById(id).orElse(null);
    }

    @Transactional
    public List<FlowerDto> saveAll(List<FlowerDto> dto) {

        List<Flower> entities = dto.stream()
                .map(FlowerMapper::toEntity)
                .toList();


        List<Flower> savedEntities = flowerRepository.saveAll(entities);


        return savedEntities.stream()
                .map(FlowerMapper::toDto)
                .toList();
    }


    public List<FlowerDto> saveAllNotTransactional(List<FlowerDto> dto) {

        List<Flower> entities = dto.stream()
                .map(FlowerMapper::toEntity)
                .toList();


        flowerRepository.saveAll(entities);

        throw new TransactionDemoException("Тест: Ошибка БЕЗ @Transactional.");
    }

    @Transactional
    public List<FlowerDto> saveAllTransactional(List<FlowerDto> dto) {

        List<Flower> entities = dto.stream()
                .map(FlowerMapper::toEntity)
                .toList();


        flowerRepository.saveAll(entities);


        throw new TransactionDemoException("Тест: Ошибка с @Transactional.");
    }

    public List<FlowerDto> findAll() {
        log.debug("Поиск всех цветов ");
        List<Flower> flowers = flowerRepository.findAll();

        return flowers.stream()
                .map(FlowerMapper::toDto)
                .toList();
    }

    public FlowerDto findById(Long id) {
        log.debug("Поиск  цветка по ID {}", id);

        Flower flower = flowerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Цветок с ID " + id + " не найден"));

        return FlowerMapper.toDto(flower);
    }

    public List<FlowerDto> findAllActive() {
        log.debug("Поиск всех активных цветов ");
        return flowerRepository.findAll().stream()
                .filter(Flower::isActive)
                .map(FlowerMapper::toDto)
                .toList();
    }

    @Transactional
    public FlowerDto create(FlowerDto dto) {
        log.info("Начало сохранения цветка под id {}", dto.getId());
        Flower flower = FlowerMapper.toEntity(dto);
        FlowerDto createFlower = FlowerMapper.toDto(flowerRepository.save(flower));
        log.info("Цветок успешно сохранен под id {}", dto.getId());

        return createFlower;
    }

    @Transactional
    public FlowerDto update(Long id, FlowerDto dto) {
        log.info("Начало изменения цветка под id {}", dto.getId());
        log.debug("Поиск  цветка  по ID {} ", id);
        Flower flower = findEntityById(id);

        double oldPrice = flower.getPrice();
        double newPrice = dto.getPrice();


        if (oldPrice != newPrice) {

            List<Bouquet> bouquets = bouquetRepository.findAllWithFlowers();

            for (Bouquet bouquet : bouquets) {

                if (bouquet.getFlowers() != null && bouquet.getFlowers().contains(flower)) {

                    double updatedBouquetPrice = bouquet.getPrice() - oldPrice + newPrice;
                    bouquet.setPrice(updatedBouquetPrice);

                }
            }
        }

        flower.setName(dto.getName());
        flower.setActive(dto.isActive());
        flower.setPrice(dto.getPrice());
        flower.setColor(Color.fromString(dto.getColor()));

        hashMap.clear();
        FlowerDto updateFlower = FlowerMapper.toDto(flowerRepository.save(flower));
        log.info(" Цветок под id {} успешно отредактирован", dto.getId());

        return updateFlower;
    }

    @Transactional
    public FlowerDto updateStatus(Long id, boolean active) {
        log.info("Начало изменения статуса цветка под id {}", id);
        log.debug("Поиск   цветка  по ID {} ", id);
        Flower flower = findEntityById(id);
        flower.setActive(active);


        if (!active) {

            List<Bouquet> bouquets = bouquetRepository.findAllWithFlowers();
            for (Bouquet bouquet : bouquets) {
                if (bouquet.getFlowers() != null && bouquet.getFlowers().contains(flower)) {
                    bouquet.setActive(false);

                }
            }
        }

        FlowerDto updateStatusFlower = FlowerMapper.toDto(flowerRepository.save(flower));
        log.info("статуса цветка под id {} успешно изменен", id);
        return updateStatusFlower;
    }

}