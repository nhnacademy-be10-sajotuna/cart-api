package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.exception.CartAlreadyExistsException;
import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.redisdto.RedisCartDto;
import com.dooray.bookstorecarts.repository.UserCartItemRepository;
import com.dooray.bookstorecarts.repository.UserCartRedisRepository;
import com.dooray.bookstorecarts.repository.UserCartRepository;
import com.dooray.bookstorecarts.response.UserCartResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCartService {
    private final UserCartRepository userCartRepository;
    private final UserCartItemService userCartItemService;
    private final UserCartItemRepository userCartItemRepository;
    private final UserCartRedisRepository userCartRedisRepository;


    public UserCartResponse getCartByUserId(Long userId) {    // 유저 아이디로 회원카트 반환
        RedisCartDto redisCart = userCartRedisRepository.findByUserId(userId);
        if (redisCart != null) {
            List<CartItem> items = redisCart.getItems().stream()
                    .map(dto -> {
                        CartItem item = new CartItem();
                        item.setId(dto.getCartItemId());
                        item.setBookId(dto.getBookId());
                        item.setQuantity(dto.getQuantity());
                        return item;
                    })
                    .collect(Collectors.toList());
            Cart cart = new Cart();
            cart.setId(redisCart.getCartId());
            cart.setUserId(redisCart.getUserId());
            return new UserCartResponse(cart, items);
        }

        Cart cart = userCartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));
        List<CartItem> items = userCartItemRepository.findByCart(cart);
        userCartRedisRepository.save(RedisCartDto.from(cart, items));
        return new UserCartResponse(cart, items);
    }

    public Cart getCartEntityByUserId(Long userId) { // 이 메서드는 무조건 db 에서만 가져오기!!
        return userCartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));
    }

    @Transactional
    public void deleteUserCart(Long userId) {
        Cart cart = getCartEntityByUserId(userId);
        userCartItemService.deleteAllCartItemsFromUserId(cart.getUserId());  // 장바구니에 비우고 카트 삭제
        userCartRepository.delete(cart);
        userCartRedisRepository.deleteByUserId(userId);
    }
}
