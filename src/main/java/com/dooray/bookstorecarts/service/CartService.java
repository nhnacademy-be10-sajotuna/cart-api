package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.redisdto.GuestCart;
import com.dooray.bookstorecarts.redisdto.GuestCartItem;
import com.dooray.bookstorecarts.redisdto.RedisCartDto;
import com.dooray.bookstorecarts.repository.UserCartItemRepository;
import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.repository.UserCartRedisRepository;
import com.dooray.bookstorecarts.repository.UserCartRepository;
import com.dooray.bookstorecarts.response.UserCartResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final GuestCartService guestCartService;
    private final UserCartRepository userCartRepository;
    private final UserCartItemRepository userCartItemRepository;
    private final UserCartRedisRepository userCartRedisRepository;

    @Transactional
    public UserCartResponse mergeCarts(Long userId, HttpSession session) {
        GuestCart guestCart = (GuestCart) session.getAttribute("guestCart");
        // 회원이 장바구니 아이템을 한번도 안담았으면 카트가 널이기때문에 카트 새로 생성
        Cart cart = getOrCreateCart(userId);
        // 게스트 카트가 없으면 병합을 건너뛰자 - 기존 유저의 카트만 반환
                if (guestCart == null) {
                    return getUserCartResponse(cart);
                }

        for (GuestCartItem guestCartItem : guestCart.getItems()) {
            CartItem cartItem = userCartItemRepository.findByCartAndBookId(cart, guestCartItem.getBookId());

            if(cartItem != null) {
                cartItem.setQuantity(cartItem.getQuantity() + guestCartItem.getQuantity());
                userCartItemRepository.save(cartItem);
            }else {
                CartItem newCartItem = new CartItem();
                newCartItem.setCart(cart);
                newCartItem.setBookId(guestCartItem.getBookId());
                newCartItem.setQuantity(guestCartItem.getQuantity());
                userCartItemRepository.save(newCartItem);
            }
        }
        guestCartService.deleteGuestCart(session);

        List<CartItem> items = userCartItemRepository.findByCart(cart);
        userCartRedisRepository.save(RedisCartDto.from(cart, items));
        return new UserCartResponse(cart, items);
    }
    // 공통 로직 분리 - 카트 조회 후 없으면 새로 생성
    private Cart getOrCreateCart(Long userId){
        return userCartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    Cart savedCart = userCartRepository.save(newCart);
                    userCartRedisRepository.save(RedisCartDto.from(savedCart, Collections.emptyList()));
                    return savedCart;
                });
    }
    // 기존 유저 카트를 반환
    private UserCartResponse getUserCartResponse(Cart cart) {
        List<CartItem> cartItems = userCartItemRepository.findByCart(cart);
        userCartRedisRepository.save(RedisCartDto.from(cart, cartItems));
        return new UserCartResponse(cart, cartItems);
    }
}
