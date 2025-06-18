//package com.dooray.bookstorecarts;
//
//import com.dooray.bookstorecarts.entity.Cart;
//import com.dooray.bookstorecarts.entity.CartItem;
//import com.dooray.bookstorecarts.exception.CartNotFoundException;
//import com.dooray.bookstorecarts.repository.UserCartItemRepository;
//import com.dooray.bookstorecarts.repository.UserCartRepository;
//import com.dooray.bookstorecarts.response.UserCartResponse;
//import com.dooray.bookstorecarts.service.UserCartItemService;
//import com.dooray.bookstorecarts.service.UserCartService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//
//@ExtendWith(MockitoExtension.class)
//public class UserCartServiceTest {
//    @Mock
//    private UserCartRepository userCartRepository;
//    @Mock
//    private UserCartItemRepository userCartItemRepository;
//    @Mock
//    private UserCartItemService userCartItemService;
//    @InjectMocks
//    private UserCartService userCartService;
//
//    @Test
//    void getCartByUserId() {
//        // Given - 테스트할 상황준비(입력, 가짜 리턴값 등)
//        Long userId = 22L;
//        Cart mockCart = new Cart();
//        mockCart.setId(1L);
//        mockCart.setUserId(userId);
//
//        CartItem mockCartItem = new CartItem();
//        mockCartItem.setId(1L);
//        mockCartItem.setBookId(1L);
//        mockCartItem.setQuantity(5L);
//
//        List<CartItem> items = List.of(mockCartItem);
//
//        given(userCartRepository.findByUserId(userId)).willReturn(Optional.of(mockCart));
//        given(userCartItemRepository.findByCart(mockCart)).willReturn(items);
//
//        // When - 실제 대상 메서드 호출
//        UserCartResponse response = userCartService.getCartByUserId(userId);
//
//        // Then - 예상 결과 검증
//        assertNotNull(response);
//        assertEquals(mockCart.getId(), response.getCartId()); // 여기 수정
//        assertEquals(mockCart.getUserId(), response.getUserId()); // 추가로 확인 가능
//        assertEquals(1, response.getItems().size());
//    }
//
//    @Test
//    void getCartByCartId() {
//        // Given
//        Long CartId = 1L;
//        Cart mockCart = new Cart();
//        mockCart.setId(CartId);
//        mockCart.setUserId(22L);
//
//        given(userCartRepository.findById(CartId)).willReturn(Optional.of(mockCart));
//
//        // When
//        Cart cart = userCartService.getCartByCartId(CartId);
//
//        // Then
//        assertNotNull(cart);
//        assertEquals(CartId, cart.getId());
//        assertEquals(22L, cart.getUserId());
//
//    }
//
//    @Test
//    void deleteUserCart() {
//        // Given
//        Long CartId = 1L;
//        Cart mockCart = new Cart();
//        mockCart.setId(CartId);
//        mockCart.setUserId(22L);
//
//        given(userCartRepository.findById(CartId)).willReturn(Optional.of(mockCart));
//
//        // When
//        userCartService.deleteUserCart(CartId);
//
//        // Then
//        verify(userCartItemService).deleteAllCartItemsFromCartId(CartId);
//        verify(userCartRepository).delete(mockCart);
//    }
//
//    @Test
//    void getCartByUserId_CartNotFound() {
//        Long userId = 22L;
//        given(userCartRepository.findByUserId(userId)).willReturn(Optional.empty());
//
//        assertThrows(CartNotFoundException.class, () -> userCartService.getCartByUserId(userId));
//    }
//
//    @Test
//    void getCartByCartId_CartNotFound() {
//        Long cartId = 22L;
//        given(userCartRepository.findById(cartId)).willReturn(Optional.empty());
//
//        assertThrows(CartNotFoundException.class, () -> userCartService.getCartByCartId(cartId));
//    }
//}
//
//
//
