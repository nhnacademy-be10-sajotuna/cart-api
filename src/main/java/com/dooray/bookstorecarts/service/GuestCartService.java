package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.exception.CartAlreadyExistsException;
import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.redisdto.GuestCart;
import com.dooray.bookstorecarts.repository.GuestCartRepository;
import com.dooray.bookstorecarts.response.GuestCartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class GuestCartService {
    private final GuestCartRepository guestCartRepository;

    public GuestCartResponse getCartBySessionId(String sessionId) {   // 세션 아이디로 비회원카트 반환
        GuestCart guestCart = guestCartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new CartNotFoundException(sessionId));

        return new GuestCartResponse(guestCart);
    }

    public void deleteGuestCart(String sessionId) {
        guestCartRepository.delete(sessionId);
    }
}
