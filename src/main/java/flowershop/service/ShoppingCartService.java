package flowershop.service;

import flowershop.entity.Bouquet;
import flowershop.entity.ShoppingCart;
import flowershop.repository.BouquetRepository;
import flowershop.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final BouquetRepository bouquetRepository;

    private ShoppingCart findEntityById(Long id) {
        return shoppingCartRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public flowershop.dto.ShoppingCartsDto getCartDto(Long id) {
        ShoppingCart cart = shoppingCartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Корзина не найдена с id: " + id));

        // Используем твой маппер для превращения сущности в DTO со списком букетов
        return flowershop.mapper.ShoppingCartMapper.toDto(cart);
    }



    @Transactional
    public void addBouquet(Long cartId, Long bouquetId) {
        ShoppingCart cart = findEntityById(cartId);
        Bouquet bouquet = bouquetRepository.findById(bouquetId).orElse(null);

        if (cart != null && bouquet != null) {

            cart.getBouquets().add(bouquet);
            shoppingCartRepository.save(cart);
        }
    }


    @Transactional
    public void removeBouquet(Long cartId, Long bouquetId) {
        ShoppingCart cart = findEntityById(cartId);
        Bouquet bouquet = bouquetRepository.findById(bouquetId).orElse(null);

        if (cart != null && bouquet != null) {
            cart.getBouquets().remove(bouquet);
            shoppingCartRepository.save(cart);
        }
    }


    @Transactional
    public void clearCart(Long cartId) {
        ShoppingCart cart = findEntityById(cartId);
        if (cart != null) {
            cart.getBouquets().clear();
            shoppingCartRepository.save(cart);
        }
    }
}
