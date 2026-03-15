package flowershop.service;

import flowershop.entity.Bouquet;
import flowershop.entity.ShoppingCart;
import flowershop.repository.BouquetRepository;
import flowershop.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final BouquetRepository bouquetRepository;

    private ShoppingCart findEntityById(Long id) {
        log.debug("Поиск сущности корзины по ID {}", id);
        return shoppingCartRepository.findById(id).orElse(null);
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

        log.info("Начало добавления букета под  id {} в корзину под id {}", bouquetId,cartId);
        log.debug("Поиск  корзины по  ID {}", cartId);
        ShoppingCart cart = findEntityById(cartId);
        log.debug("Поиск  букета по  ID {}", bouquetId);
        Bouquet bouquet = bouquetRepository.findById(bouquetId).orElse(null);

        if (bouquet != null) {

            cart.getBouquets().add(bouquet);
            shoppingCartRepository.save(cart);
            log.info("Добавление букета под  id {} в корзину под id {} успешно завершено", bouquetId,cartId);
        }
        log.warn("Букет с id {} не найден", cartId);
    }


    @Transactional
    public void removeBouquet(Long cartId, Long bouquetId) {

        log.info("Начало удаления букета под  id {} из корзины под id {}", bouquetId,cartId);
        log.debug("Поиск  корзины  по  ID {}", cartId);
        ShoppingCart cart = findEntityById(cartId);
        Bouquet bouquet = bouquetRepository.findById(bouquetId).orElse(null);

        if (bouquet != null) {
            cart.getBouquets().remove(bouquet);
            shoppingCartRepository.save(cart);
            log.info("Удаление букета под  id {} из корзины под id {} произошло успешно", bouquetId,cartId);
        }
        log.warn("Букет с id {} не найден и не удален с корзины", cartId);
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
