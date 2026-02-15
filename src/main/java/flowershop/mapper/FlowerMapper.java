package flower.shop.mapper;

import flower.shop.dto.FlowerDto;
import flower.shop.entity.Flower;

public class FlowerMapper {

  public static FlowerDto toDto(Flower flower) {

    if (flower == null) {
      return null;
    }

    FlowerDto flowerDto = new FlowerDto();
    flowerDto.setCatalogNumber(flower.getCatalogNumber());
    flowerDto.setName(flower.getName());
    flowerDto.setPrice(flower.getPrice());
    flowerDto.setColor(flower.getColor());

    return flowerDto;
  }
}
