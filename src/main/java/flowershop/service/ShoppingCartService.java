package flowershop.service;

import flowershop.entity.ShoppingCart;
import flowershop.repository.BouquetRepository;
import flowershop.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final BouquetRepository bouquetRepository;

    private ShoppingCart findEntityById(Long id) {
        return shoppingCartRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Корзина не найдена: " + id));
    }

    @Transactional(readOnly = true)
    public flowershop.dto.ShoppingCartsDto getCartDto(Long id) {
        log.debug("Поиск  корзины по ID {}", id);
        ShoppingCart cart = shoppingCartRepository.findById(id)
                .orElseThrow();

        return flowershop.mapper.ShoppingCartMapper.toDto(cart);
    }


    @Transactional
    public void addBouquet(Long cartId, Long bouquetId) {

        log.info("Начало добавления букета {} в корзину {}", bouquetId, cartId);

        ShoppingCart cart = findEntityById(cartId);

        bouquetRepository.findById(bouquetId).ifPresentOrElse(
                bouquet -> {
                    cart.getBouquets().add(bouquet);
                    shoppingCartRepository.save(cart);
                    log.info("Букет {} успешно добавлен в корзину {}", bouquetId, cartId);
                },
                () -> log.warn("Букет с id {} не найден, добавление невозможно", bouquetId)
        );
    }


    @Transactional
    public void removeBouquet(Long cartId, Long bouquetId) {

        log.info("Начало удаления букета {} из корзины {}", bouquetId, cartId);

        ShoppingCart cart = findEntityById(cartId);

        bouquetRepository.findById(bouquetId).ifPresentOrElse(
                bouquet -> {
                    cart.getBouquets().remove(bouquet);
                    shoppingCartRepository.save(cart);
                    log.info("Букет {} успешно удален из корзины {}", bouquetId, cartId);
                },
                () -> log.warn("Букет с id {} не найден, удаление не произошло", bouquetId)
        );
    }


    @Transactional
    public void clearCart(Long cartId) {
        log.info("Начало очищения корзины под id {}",cartId);
        log.debug("Поиск корзины  по  ID {}", cartId);
        ShoppingCart cart = findEntityById(cartId);

        cart.getBouquets().clear();
        shoppingCartRepository.save(cart);
        log.info("Очищение корзины под id {} произошло успешно",cartId);
    }
}
