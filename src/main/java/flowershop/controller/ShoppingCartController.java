package flowershop.controller;

import flowershop.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import flowershop.dto.ShoppingCartsDto;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;


    @PostMapping("/{cartId}/add/{bouquetId}")
    public void addBouquet(@PathVariable Long cartId, @PathVariable Long bouquetId) {
        shoppingCartService.addBouquet(cartId, bouquetId);
    }


    @DeleteMapping("/{cartId}/remove/{bouquetId}")
    public void removeBouquet(@PathVariable Long cartId, @PathVariable Long bouquetId) {
        shoppingCartService.removeBouquet(cartId, bouquetId);
    }


    @DeleteMapping("/{cartId}/clear")
    public void clearCart(@PathVariable Long cartId) {
        shoppingCartService.clearCart(cartId);
    }

    @GetMapping("/{cartId}")
    public ShoppingCartsDto getCart(@PathVariable Long cartId) {
        return shoppingCartService.getCartDto(cartId);
    }
}