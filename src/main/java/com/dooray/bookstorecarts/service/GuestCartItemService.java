package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.exception.*;
import com.dooray.bookstorecarts.redisdto.GuestCart;
import com.dooray.bookstorecarts.redisdto.GuestCartItem;
import com.dooray.bookstorecarts.repository.GuestCartRepository;
import com.dooray.bookstorecarts.request.CartItemRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestCartItemService {
    private final GuestCartRepository guestCartRepository;

    public GuestCartItem createGuestCartItem(String sessionId, CartItemRequest request) {
        try {
            GuestCart guestCart = guestCartRepository.findBySessionId(sessionId);

            if (guestCart == null) {
                guestCart = new GuestCart(sessionId, new ArrayList<>());
            }

            for (GuestCartItem existingItem : guestCart.getItems()) {
                if (existingItem.getBookId().equals(request.getBookId())) {
                    existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
                    guestCartRepository.save(guestCart);
                    return existingItem;
                }
            }

            GuestCartItem newItem = new GuestCartItem();
            newItem.setBookId(request.getBookId());
            newItem.setQuantity(request.getQuantity());
            guestCart.getItems().add(newItem);

            guestCartRepository.save(guestCart);
            return newItem;

        } catch (JsonProcessingException e) {
            throw new GuestCartSaveException("비회원 장바구니 저장 실패: " + e.getMessage());
        }
    }

    // 회원은 카트아이템(기본키, 오토인크리즈먼트키)로 식별되는데 비회원은 기본키없어서 session Id와 book id가 둘다 있어야 식별가능
    public GuestCartItem getGuestCartItemByBookId(String sessionId, Long BookId){
        try{
            GuestCart guestCart = guestCartRepository.findBySessionId(sessionId);
            if (guestCart == null) {
                throw new CartNotFoundException(sessionId);
            }
            for(GuestCartItem item : guestCart.getItems()){
                if(item.getBookId().equals(BookId)){
                    return item;
                }
            }

            throw new CartItemNotFoundException(BookId);
        }catch(JsonProcessingException e){
            throw new GuestCartReadException("GuestCart 읽기 중 오류 발생: "+e.getMessage());
        }
    }

    public List<GuestCartItem> getGuestCartItemsBySessionId(String sessionId) {
        try{
            GuestCart guestCart = guestCartRepository.findBySessionId(sessionId);
            return guestCart != null ? guestCart.getItems() : List.of();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public GuestCartItem updateQuantity(String sessionId, CartItemRequest request) {
        try {
            GuestCart guestCart = guestCartRepository.findBySessionId(sessionId);
            if (guestCart == null) throw new CartNotFoundException("게스트 카트가 없습니다.");
            if (request.getQuantity() <= 0) throw new InvalidException("수량은 1 이상이어야 합니다.");

            GuestCartItem guestCartItem = null;
            for (GuestCartItem item : guestCart.getItems()) {
                if (item.getBookId().equals(request.getBookId())) {
                    guestCartItem = item;
                    break;
                }
            }

            if (guestCartItem == null) throw new CartItemNotFoundException();

            guestCartItem.setQuantity(request.getQuantity());
            guestCartRepository.save(guestCart);

            return guestCartItem;
        } catch (JsonProcessingException e) {
            throw new GuestCartSaveException("게스트 카트 수량 변경 실패: " + e.getMessage());
        }
    }

    public void deleteGuestCartItem(String sessionId, Long bookId) {
        try {
            GuestCart guestCart = guestCartRepository.findBySessionId(sessionId);
            if (guestCart == null) return;

            guestCart.getItems().removeIf(item -> item.getBookId().equals(bookId));
            guestCartRepository.save(guestCart);
        } catch (JsonProcessingException e) {
            throw new GuestCartSaveException("게스트 카트 아이템 삭제 실패: " + e.getMessage());
        }
    }
}
