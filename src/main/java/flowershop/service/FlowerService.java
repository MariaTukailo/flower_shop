package flowershop.service;

import flowershop.dto.FlowerDto;
import flowershop.entity.Flower;
import flowershop.mapper.FlowerMapper;
import flowershop.repository.FlowerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FlowerService {

  private final FlowerRepository flowerRepository;

  public List<FlowerDto> getAllFlowers() {
    return flowerRepository.findAll().stream().map(FlowerMapper::toDto).toList();
  }

  public FlowerDto findFlowerByCatalogNumber(int id) {

    Flower flowerOther = flowerRepository.findAll().stream()
        .filter(flower -> flower.getCatalogNumber() == id)
        .findFirst().orElse(null);

    return FlowerMapper.toDto(flowerOther);
  }

  public List<FlowerDto> findFlowersByColor(String color) {

    if (color == null || color.isEmpty()) {
      return getAllFlowers();
    }

    return flowerRepository.findAll()
       .stream()
       .filter(flower -> flower.getColor().equalsIgnoreCase(color))
       .map(FlowerMapper::toDto)
       .toList();
  }

}
