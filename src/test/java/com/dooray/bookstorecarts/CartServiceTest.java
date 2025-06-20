package com.dooray.bookstorecarts;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.redisdto.GuestCart;
import com.dooray.bookstorecarts.redisdto.GuestCartItem;
import com.dooray.bookstorecarts.redisdto.RedisCartDto;
import com.dooray.bookstorecarts.repository.UserCartItemRepository;
import com.dooray.bookstorecarts.repository.UserCartRedisRepository;
import com.dooray.bookstorecarts.repository.UserCartRepository;
import com.dooray.bookstorecarts.response.UserCartResponse;
import com.dooray.bookstorecarts.service.CartService;
import com.dooray.bookstorecarts.service.GuestCartService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
    @Mock
    private GuestCartService guestCartService;
    @Mock
    private UserCartRepository userCartRepository;
    @Mock
    private UserCartItemRepository userCartItemRepository;
    @Mock
    private UserCartRedisRepository userCartRedisRepository;
    @Mock
    private HttpSession session;
    @InjectMocks
    private CartService cartService;

    @Test
    void mergeCarts(){
        // given
        Long userId = 22L;

        GuestCartItem item = new GuestCartItem(1L, 3L);
        GuestCart mockGuestCart = new GuestCart("test-session-id", List.of(item));
        given(session.getAttribute("guestCart")).willReturn(mockGuestCart);

        Cart mockCart = new Cart();
        mockCart.setId(100L);
        mockCart.setUserId(userId);
        given(userCartRepository.findByUserId(userId)).willReturn(Optional.of(mockCart));

        given(userCartItemRepository.findByCartAndBookId(mockCart,1L)).willReturn(null);

        CartItem cartItem = new CartItem();
        cartItem.setId(200L);
        cartItem.setBookId(1L);
        cartItem.setQuantity(3L);
        cartItem.setCart(mockCart);
        given(userCartItemRepository.save(any())).willReturn(cartItem);

        given(userCartItemRepository.findByCart(mockCart)).willReturn(List.of(cartItem));

        // when
        UserCartResponse result = cartService.mergeCarts(userId, session);
        // then
        assert result != null;
        assert result.getUserId().equals(userId);
        assert result.getItems().size() == 1;
        assert result.getItems().get(0).getBookId().equals(1L);
    }
}
