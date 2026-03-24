package flowershop.service;

import flowershop.dto.ShoppingCartsDto;
import flowershop.entity.Bouquet;
import flowershop.entity.ShoppingCart;
import flowershop.repository.BouquetRepository;
import flowershop.repository.ShoppingCartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private BouquetRepository bouquetRepository;

    @InjectMocks
    private ShoppingCartService shoppingCartService;

    private ShoppingCart cart;
    private Bouquet bouquet;

    @BeforeEach
    void setUp() {
        bouquet = new Bouquet();
        bouquet.setId(10L);

        cart = new ShoppingCart();
        cart.setId(1L);
        cart.setBouquets(new ArrayList<>());
    }

    @Test
    void getCartDto_Success() {
        when(shoppingCartRepository.findById(1L)).thenReturn(Optional.of(cart));


        ShoppingCartsDto result = shoppingCartService.getCartDto(1L);

        assertNotNull(result);
        verify(shoppingCartRepository).findById(1L);
    }

    @Test
    void addBouquet_Success() {
        when(shoppingCartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(bouquetRepository.findById(10L)).thenReturn(Optional.of(bouquet));

        shoppingCartService.addBouquet(1L, 10L);

        assertTrue(cart.getBouquets().contains(bouquet));
        verify(shoppingCartRepository).save(cart);
    }

    @Test
    void addBouquet_BouquetNotFound() {
        when(shoppingCartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(bouquetRepository.findById(10L)).thenReturn(Optional.empty());


        shoppingCartService.addBouquet(1L, 10L);

        assertFalse(cart.getBouquets().contains(bouquet));
        verify(shoppingCartRepository, never()).save(any());
    }

    @Test
    void removeBouquet_Success() {
        cart.getBouquets().add(bouquet);
        when(shoppingCartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(bouquetRepository.findById(10L)).thenReturn(Optional.of(bouquet));

        shoppingCartService.removeBouquet(1L, 10L);

        assertFalse(cart.getBouquets().contains(bouquet));
        verify(shoppingCartRepository).save(cart);
    }

    @Test
    void removeBouquet_NotFound() {
        when(shoppingCartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(bouquetRepository.findById(10L)).thenReturn(Optional.empty());

        shoppingCartService.removeBouquet(1L, 10L);

        verify(shoppingCartRepository, never()).save(any());
    }

    @Test
    void clearCart_Success() {
        cart.getBouquets().add(bouquet);
        when(shoppingCartRepository.findById(1L)).thenReturn(Optional.of(cart));

        shoppingCartService.clearCart(1L);

        assertTrue(cart.getBouquets().isEmpty());
        verify(shoppingCartRepository).save(cart);
    }

    @Test
    void findEntityById_ThrowsException() {
        when(shoppingCartRepository.findById(1L)).thenReturn(Optional.empty());


        assertThrows(ResponseStatusException.class, () -> shoppingCartService.clearCart(1L));
    }
}