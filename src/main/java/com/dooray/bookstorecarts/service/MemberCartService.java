package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.exception.CartAlreadyExistsException;
import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.repository.MemberCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberCartService {
    private final MemberCartRepository memberCartRepository;
    private final MemberCartItemService memberCartItemService;

    public Cart createMemberCart(Long userId){       // 회원 카트 생성(db 저장)
        if (memberCartRepository.existsByUserId(userId)){
            throw new CartAlreadyExistsException(userId);
        }
        Cart newCart = new Cart();
        newCart.setUserId(userId);
        return memberCartRepository.save(newCart);
    }

    public Cart getCartByUserId(Long userId) {    // 유저 아이디로 회원카트 반환
        return memberCartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));
    }

    public Cart getCartByCartId(Long cartId) {
        return memberCartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(cartId));
    }

    public void deleteMemberCart(Long cartId) {
        Cart cart = getCartByCartId(cartId);
        memberCartItemService.deleteAllCartItemsFromCart(cart.getId());  // 장바구니에 비우고 카트 삭제
        memberCartRepository.delete(cart);
    }
}
