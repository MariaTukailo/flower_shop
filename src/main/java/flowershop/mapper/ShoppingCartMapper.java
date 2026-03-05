package flowershop.mapper;

import flowershop.dto.ShoppingCartsDto;
import flowershop.entity.ShoppingCart;
import java.util.ArrayList;

public class ShoppingCartMapper {

    private ShoppingCartMapper() { }

    public static ShoppingCartsDto toDto(ShoppingCart cart) {
        if (cart == null) {
            return null;
        }

        ShoppingCartsDto dto = new ShoppingCartsDto();

        if (cart.getCustomer() != null) {
            dto.setCustomerId(cart.getCustomer().getId());
        }

        if (cart.getBouquets() != null) {
            dto.setBouquets(cart.getBouquets().stream()
                    .map(BouquetMapper::toDto)
                    .toList());
        }

        return dto;
    }

    public static ShoppingCart toEntity(ShoppingCartsDto dto) {
        if (dto == null) {
            return null;
        }

        ShoppingCart cart = new ShoppingCart();
        cart.setBouquets(new ArrayList<>());

        return cart;
    }
}
