package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.exception.CartAlreadyExistsException;
import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.exception.GuestCartReadException;
import com.dooray.bookstorecarts.exception.GuestCartSaveException;
import com.dooray.bookstorecarts.redisdto.GuestCart;
import com.dooray.bookstorecarts.repository.GuestCartRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuestCartService {
    private final GuestCartRepository guestCartRepository;

    public GuestCart createGuestCart(String sessionId){  // 비회원 카트 생성(레디스 저장)
        if(guestCartRepository.existsBySessionId(sessionId)){
            throw new CartAlreadyExistsException(sessionId);
        }

        GuestCart guestCart = new GuestCart(sessionId, new ArrayList<>());
            return guestCartRepository.save(guestCart);
    }

    public GuestCart getCartBySessionId(String sessionId) {   // 세션 아이디로 비회원카트 반환
        return guestCartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new CartNotFoundException(sessionId));
    }

    public void deleteGuestCart(String sessionId) {
        guestCartRepository.delete(sessionId);
    }
}
