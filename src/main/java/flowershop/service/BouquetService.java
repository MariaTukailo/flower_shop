package flowershop.service;

import flowershop.dto.BouquetDto;
import flowershop.dto.FlowerDto;
import flowershop.entity.Bouquet;
import flowershop.entity.Flower;
import flowershop.mapper.BouquetMapper;
import flowershop.repository.BouquetRepository;
import flowershop.repository.FlowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BouquetService {

    private final BouquetRepository bouquetRepository;
    private final FlowerRepository flowerRepository;

    private Bouquet findEntityById(Long id) {
        return bouquetRepository.findById(id).orElse(null);
    }

    public List<BouquetDto> findAll() {
        List<Bouquet> bouquets = bouquetRepository.findAll();

        return bouquets.stream()
                .map(BouquetMapper::toDto)
                .toList();
    }

    public List<BouquetDto> findAllActive() {
        return bouquetRepository.findAll().stream()
                .filter(Bouquet::isActive)
                .map(BouquetMapper::toDto)
                .toList();
    }

    public BouquetDto findById(Long id) {
        Bouquet bouquet = bouquetRepository.findById(id).orElse(null);
        if (bouquet == null) {
            return null;
        }

        return BouquetMapper.toDto(bouquet);
    }

    public List<BouquetDto> findAllOptimized() {
        return bouquetRepository.findAllWithFlowers().stream()
                .map(BouquetMapper::toDto)
                .toList();
    }

    @Transactional
    public BouquetDto create(BouquetDto dto) {

        List<Long> flowerIds = dto.getFlowers().stream()
                .map(FlowerDto::getId)
                .toList();


        List<Flower> allRequestedFlowers = flowerRepository.findAllById(flowerIds);
        List<Flower> activeFlowers = new ArrayList<>();
        double price = 0;

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

        return BouquetMapper.toDto(bouquetRepository.save(bouquet));
    }

    @Transactional
    public BouquetDto update(Long id, BouquetDto dto) {

        Bouquet bouquet = findEntityById(id);

        bouquet.setName(dto.getName());
        bouquet.setActive(dto.isActive());
        bouquet.setWrappingPaper(dto.isWrappingPaper());
        bouquet.setRibbon(dto.isRibbon());

        if (dto.getFlowers() != null) {

            List<Long> flowerIds = dto.getFlowers().stream()
                    .map(FlowerDto::getId)
                    .toList();


            List<Flower> allRequestedFlowers = flowerRepository.findAllById(flowerIds);
            List<Flower> activeFlowers = new ArrayList<>();
            double price = 0;

            for (Flower flower : allRequestedFlowers) {
                if (flower.isActive()) {
                    activeFlowers.add(flower);
                    price += flower.getPrice();
                }
            }

            bouquet.setFlowers(activeFlowers);
            bouquet.setPrice(price);
        }

        return BouquetMapper.toDto(bouquetRepository.save(bouquet));
    }

    @Transactional
    public BouquetDto updateStatus(Long id, boolean active) {

        Bouquet bouquet = findEntityById(id);



        if (active) {
            for (Flower flower : bouquet.getFlowers()) {

                if (!flower.isActive()) {

                    return BouquetMapper.toDto(bouquet);
                }
            }
        }
        bouquet.setActive(active);
        return BouquetMapper.toDto(bouquetRepository.save(bouquet));
    }
}