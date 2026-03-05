package flowershop.service;

import flowershop.dto.FlowerDto;
import flowershop.enums.Color;
import flowershop.mapper.FlowerMapper;
import flowershop.entity.Bouquet;
import flowershop.entity.Flower;
import flowershop.repository.FlowerRepository;
import flowershop.repository.BouquetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlowerService {

    private final FlowerRepository flowerRepository;
    private final BouquetRepository bouquetRepository;

    private Flower findEntityById(Long id) {

        return flowerRepository.findById(id).orElse(null);
    }

    public List<FlowerDto> findAll() {
        List<Flower> flowers = flowerRepository.findAll();

        return flowers.stream()
                .map(FlowerMapper::toDto)
                .toList();
    }

    public FlowerDto findById(Long id) {
        Flower flower = flowerRepository.findById(id).orElse(null);
        if (flower == null) {
            return null;
        }

        return FlowerMapper.toDto(flower);
    }

    public List<FlowerDto> findAllActive() {
        return flowerRepository.findAll().stream()
                .filter(Flower::isActive)
                .map(FlowerMapper::toDto)
                .toList();
    }

    @Transactional
    public FlowerDto create(FlowerDto dto) {

        Flower flower = FlowerMapper.toEntity(dto);
        return FlowerMapper.toDto(flowerRepository.save(flower));
    }

    @Transactional
    public FlowerDto update(Long id, FlowerDto dto) {

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

        return FlowerMapper.toDto(flowerRepository.save(flower));
    }

    @Transactional
    public FlowerDto updateStatus(Long id, boolean active) {

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

        return FlowerMapper.toDto(flowerRepository.save(flower));
    }

}