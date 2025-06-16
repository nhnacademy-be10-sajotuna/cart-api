package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.exception.*;
import com.dooray.bookstorecarts.redisdto.GuestCart;
import com.dooray.bookstorecarts.redisdto.GuestCartItem;
import com.dooray.bookstorecarts.repository.GuestCartRepository;
import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.response.GuestCartItemResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestCartItemService {
    private final GuestCartRepository guestCartRepository;

    @Transactional
    public GuestCartItemResponse createGuestCartItem(String sessionId, CartItemRequest request) {
            GuestCart guestCart = guestCartRepository.findBySessionId(sessionId)
                    .orElseGet(() -> new GuestCart(sessionId, new ArrayList<>()));

            for (GuestCartItem existingItem : guestCart.getItems()) {
                if (existingItem.getBookId().equals(request.getBookId())) {
                    existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
                    guestCartRepository.save(guestCart);
                    return new GuestCartItemResponse(existingItem);
                }
            }

            GuestCartItem newItem = new GuestCartItem();
            newItem.setBookId(request.getBookId());
            newItem.setQuantity(request.getQuantity());
            guestCart.getItems().add(newItem);

            guestCartRepository.save(guestCart);
            return new GuestCartItemResponse(newItem);
    }

    // 회원은 카트아이템(기본키, 오토인크리즈먼트키)로 식별되는데 비회원은 기본키없어서 session Id와 book id가 둘다 있어야 식별가능
    public GuestCartItemResponse getGuestCartItemByBookId(String sessionId, Long BookId){
            GuestCart guestCart = guestCartRepository.findBySessionId(sessionId)
                    .orElseThrow(() -> new CartNotFoundException(sessionId));

            for(GuestCartItem item : guestCart.getItems()){
                if(item.getBookId().equals(BookId)){
                    return new GuestCartItemResponse(item);
                }
            }
            throw new CartItemNotFoundException(BookId);
    }

    public List<GuestCartItem> getGuestCartItemsBySessionId(String sessionId) {
        return guestCartRepository.findBySessionId(sessionId)
                .map(GuestCart::getItems)
                .orElse(List.of());
    }

    @Transactional
    public GuestCartItemResponse updateQuantity(String sessionId, CartItemRequest request) {
            GuestCart guestCart = guestCartRepository.findBySessionId(sessionId)
                    .orElseThrow(() -> new CartNotFoundException(sessionId));
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

            return new GuestCartItemResponse(guestCartItem);
    }
    @Transactional
    public void deleteGuestCartItem(String sessionId, Long bookId) {
        guestCartRepository.findBySessionId(sessionId).ifPresent(guestCart -> {
            guestCart.getItems().removeIf(item -> item.getBookId().equals(bookId));
            guestCartRepository.save(guestCart);
        });
    }
    @Transactional
    public void deleteAllGuestCartItems(String sessionId) {
        guestCartRepository.findBySessionId(sessionId).ifPresent(guestCart -> {
            guestCart.getItems().clear();
            guestCartRepository.save(guestCart);
        });
    }
}
