package flowershop.service;

import flowershop.dto.BouquetDto;
import flowershop.dto.FlowerDto;
import flowershop.entity.Bouquet;
import flowershop.entity.Flower;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import flowershop.mapper.BouquetMapper;
import flowershop.repository.BouquetRepository;
import flowershop.repository.FlowerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BouquetService {

    private final BouquetRepository bouquetRepository;
    private final FlowerRepository flowerRepository;


    private Bouquet findEntityById(Long id) {
        log.debug("Поиск букета по ID: {}", id);
        return bouquetRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Букет с ID " + id + " не найден"));

    }

    public List<BouquetDto> findAll() {
        log.debug("Поиск всех букетов");
        List<Bouquet> bouquets = bouquetRepository.findAll();

        return bouquets.stream()
                .map(BouquetMapper::toDto)
                .toList();
    }

    public List<BouquetDto> findAllActive() {
        log.debug("Поиск всех активных букетов");
        return bouquetRepository.findAll().stream()
                .filter(Bouquet::isActive)
                .map(BouquetMapper::toDto)
                .toList();
    }

    public BouquetDto findById(Long id) {
        log.debug("Поиск  букета по ID: {}", id);
        Bouquet bouquet = bouquetRepository.findById(id).orElseThrow(() -> new EntityNotFoundException( "Букет с ID " + id + " не найден"));


        return BouquetMapper.toDto(bouquet);
    }

    public List<BouquetDto> findAllOptimized() {
        log.debug("Оптимизированный поиск всех букетов ");
        return bouquetRepository.findAllWithFlowers().stream()
                .map(BouquetMapper::toDto)
                .toList();
    }

    @Transactional
    public BouquetDto create(BouquetDto dto) {
        log.info("Начало процесса создания нового букета: {}", dto.getName());


        List<Long> flowerIds = dto.getFlowers().stream()
                .map(FlowerDto::getId)
                .toList();

        log.debug("Запрошены цветы с ID: {}", flowerIds);
        List<Flower> allRequestedFlowers = flowerRepository.findAllById(flowerIds);
        List<Flower> activeFlowers = new ArrayList<>();
        double price = 0;

        log.debug("Расчет цены букета");
        for (Flower flower : allRequestedFlowers) {

            if (flower.isActive()) {
                activeFlowers.add(flower);
                price += flower.getPrice();
            }

        }

        Bouquet bouquet = BouquetMapper.toEntity(dto);
        bouquet.setFlowers(activeFlowers);
        bouquet.setPrice(price);
        bouquet.setActive(true);

        log.info("Букет успешно сформирован. Итоговая цена: {}, Количество активных цветов: {}",
                price, activeFlowers.size());

        Bouquet savedBouquet = bouquetRepository.save(bouquet);

        log.info("Букет успешно сохранен в БД под ID: {}", savedBouquet.getId());

        return BouquetMapper.toDto(savedBouquet);
    }


    @Transactional
    public BouquetDto updateStatus(Long id, boolean active) {
        log.info("Начало процесса изменения букета: {}", id);
        Bouquet bouquet = findEntityById(id);

        if (bouquet == null) {
            log.warn("Букет не  найден по ID : {}", id);
            return null;
        }

        if (active) {

            for (Flower flower : bouquet.getFlowers()) {

                if (!flower.isActive()) {

                    return BouquetMapper.toDto(bouquet);
                }
            }
        }

        bouquet.setActive(active);
        BouquetDto bouquetSave = BouquetMapper.toDto(bouquetRepository.save(bouquet));
        log.info("Статус букета успешно изменен в БД : ");
        return bouquetSave;
    }
}