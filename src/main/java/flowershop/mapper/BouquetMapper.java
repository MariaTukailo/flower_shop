package flowershop.mapper;

import flowershop.dto.BouquetDto;
import flowershop.dto.FlowerDto;
import flowershop.entity.Bouquet;
import flowershop.entity.Flower;

import java.util.ArrayList;
import java.util.List;

public class BouquetMapper {

    private BouquetMapper() {
    }

    public static BouquetDto toDto(Bouquet bouquet) {

        if (bouquet == null) {
            return null;
        }

        BouquetDto dto = new BouquetDto();
        dto.setId(bouquet.getId());
        dto.setName(bouquet.getName());
        dto.setActive(bouquet.isActive());
        dto.setPrice(bouquet.getPrice());
        dto.setWrappingPaper(bouquet.isWrappingPaper());
        dto.setRibbon(bouquet.isRibbon());
        dto.setCountFlowers(bouquet.getCountFlowers());

        if (bouquet.getFlowers() != null) {
            List<FlowerDto> flowerDto = bouquet.getFlowers().stream()
                    .map(FlowerMapper::toDto)
                    .toList();
            dto.setFlowers(flowerDto);
        }

        return dto;
    }

    public static Bouquet toEntity(BouquetDto dto) {
        if (dto == null) {
            return null;
        }

        Bouquet bouquet = new Bouquet();
        bouquet.setId(dto.getId());
        bouquet.setName(dto.getName());
        bouquet.setActive(dto.isActive());
        bouquet.setPrice(dto.getPrice());
        bouquet.setWrappingPaper(dto.isWrappingPaper());
        bouquet.setRibbon(dto.isRibbon());
        bouquet.setCountFlowers(dto.getCountFlowers());

        if (dto.getFlowers() != null) {
            List<Flower> flowers = dto.getFlowers().stream()
                    .map(FlowerMapper::toEntity)
                    .toList();
            bouquet.setFlowers(flowers);
        } else {
            bouquet.setFlowers(new ArrayList<>());
        }

        return bouquet;
    }
}
