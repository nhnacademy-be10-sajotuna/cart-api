package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.exception.CartAlreadyExistsException;
import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.repository.UserCartItemRepository;
import com.dooray.bookstorecarts.repository.UserCartRepository;
import com.dooray.bookstorecarts.response.UserCartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCartService {
    private final UserCartRepository userCartRepository;
    private final UserCartItemService userCartItemService;
    private final UserCartItemRepository userCartItemRepository;

    public UserCartResponse createUserCart(Long userId){       // 회원 카트 생성(db 저장)
        if (userCartRepository.existsByUserId(userId)){
            throw new CartAlreadyExistsException(userId);
        }
        Cart newCart = new Cart();
        newCart.setUserId(userId);
        List<CartItem> emptyItems = List.of();
        return new UserCartResponse(userCartRepository.save(newCart), emptyItems);
    }

    public UserCartResponse getCartByUserId(Long userId) {    // 유저 아이디로 회원카트 반환
        Cart cart = userCartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));

        List<CartItem> items = userCartItemRepository.findByCart(cart);
        return new UserCartResponse(cart, items);
    }

    public Cart getCartByCartId(Long cartId) {
        return userCartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(cartId));
    }

    public void deleteUserCart(Long cartId) {
        Cart cart = getCartByCartId(cartId);
        userCartItemService.deleteAllCartItemsFromCart(cart.getId());  // 장바구니에 비우고 카트 삭제
        userCartRepository.delete(cart);
    }
}
