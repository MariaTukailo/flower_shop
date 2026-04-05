package flowershop.mapper;

import flowershop.dto.FlowerDto;
import flowershop.entity.Flower;
import flowershop.enums.Color;

public class FlowerMapper {

    private FlowerMapper() { }

    public static FlowerDto toDto(Flower flower) {

        if (flower == null) {
            return null;
        }

        FlowerDto flowerDto = new FlowerDto();
        flowerDto.setId(flower.getId());
        flowerDto.setName(flower.getName());
        flowerDto.setActive(flower.isActive());
        flowerDto.setPathPhoto((flower.getPathPhoto()));
        flowerDto.setPrice(flower.getPrice());
        if (flower.getColor() != null) {
            flowerDto.setColor(flower.getColor().name());
        }

        return flowerDto;
    }

    public static Flower toEntity(FlowerDto flowerDto) {
        if (flowerDto == null) {
            return null;
        }

        Flower flower = new Flower();
        flower.setId(flowerDto.getId());
        flower.setName(flowerDto.getName());
        flower.setPrice(flowerDto.getPrice());
        flower.setPathPhoto(flowerDto.getPathPhoto());
        flower.setActive(flowerDto.isActive());
        flower.setColor(Color.fromString(flowerDto.getColor()));

        return flower;
    }
}


