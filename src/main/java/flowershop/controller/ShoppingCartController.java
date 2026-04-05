package flowershop.controller;

import flowershop.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import flowershop.dto.ShoppingCartsDto;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Управление корзинами покупателей", description = "Методы для работы с корзинами покупателей")
@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "Добавить букет в корзину ", description = "Добавляет букет в корзину ")
    @PostMapping("/{cartId}/add/{bouquetId}")
    public void addBouquet(@PathVariable Long cartId,@Valid @PathVariable Long bouquetId) {
        shoppingCartService.addBouquet(cartId, bouquetId);
    }

    @Operation(summary = "Удалить букет из корзины ", description = "Удаляет букет из корзины ")
    @DeleteMapping("/{cartId}/remove/{bouquetId}")
    public void removeBouquet(@PathVariable Long cartId, @PathVariable Long bouquetId) {
        shoppingCartService.removeBouquet(cartId, bouquetId);
    }

    @Operation(summary = "Очистить корзину", description = "Очищает корзину полностью ")
    @DeleteMapping("/{cartId}/clear")
    public void clearCart(@PathVariable Long cartId) {
        shoppingCartService.clearCart(cartId);
    }

    @Operation(summary = "Получить корзину по ID ", description = "Возвращает корзину по ID ")
    @GetMapping("/{cartId}")
    public ShoppingCartsDto getCart(@PathVariable Long cartId) {
        return shoppingCartService.getCartDto(cartId);
    }
}