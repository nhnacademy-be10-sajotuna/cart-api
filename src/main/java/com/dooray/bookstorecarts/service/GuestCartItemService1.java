package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.exception.CartItemNotFoundException;
import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.redisdto.GuestCart;
import com.dooray.bookstorecarts.redisdto.GuestCartItem;
import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.response.GuestCartItemResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class GuestCartItemService1 {

    @Transactional
    public GuestCartItemResponse createGuestCartItem(HttpSession session, CartItemRequest request) {
        GuestCart guestCart = (GuestCart) session.getAttribute("guestCart");
        if (guestCart == null) {
            guestCart = new GuestCart(session.getId(), new ArrayList<>());
        }

        for (GuestCartItem existingItem : guestCart.getItems()) {
            if (existingItem.getBookId().equals(request.getBookId())) {
                existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
                session.setAttribute("guestCart", guestCart);
                return new GuestCartItemResponse(existingItem);
            }
        }

        GuestCartItem newItem = new GuestCartItem();
        newItem.setBookId(request.getBookId());
        newItem.setQuantity(request.getQuantity());
        guestCart.getItems().add(newItem);

        session.setAttribute("guestCart", guestCart);
        return new GuestCartItemResponse(newItem);
    }

    // 회원은 카트아이템(기본키, 오토인크리즈먼트키)로 식별되는데 비회원은 기본키없어서 session Id와 book id가 둘다 있어야 식별가능
    public GuestCartItemResponse getGuestCartItemByBookId(HttpSession session, Long BookId){
        GuestCart guestCart = (GuestCart) session.getAttribute("guestCart");
        if (guestCart == null) {
            throw new CartNotFoundException(session.getId());
        }
        for(GuestCartItem item : guestCart.getItems()){
            if(item.getBookId().equals(BookId)){
                return new GuestCartItemResponse(item);
            }
        }
        throw CartItemNotFoundException.forBookId(BookId);
    }

    @Transactional
    public GuestCartItemResponse updateQuantity(HttpSession session, CartItemRequest request) {
        GuestCart guestCart = (GuestCart) session.getAttribute("guestCart");
        if (guestCart == null) {
            throw new CartNotFoundException(session.getId());
        }

        GuestCartItem guestCartItem = null;
        for (GuestCartItem item : guestCart.getItems()) {
            if (item.getBookId().equals(request.getBookId())) {
                guestCartItem = item;
                break;
            }
        }

        if (guestCartItem == null) throw  CartItemNotFoundException.forBookId(request.getBookId());
        guestCartItem.setQuantity(request.getQuantity());
        session.setAttribute("guestCart", guestCart);

        return new GuestCartItemResponse(guestCartItem);
    }

    @Transactional
    public void deleteGuestCartItem(HttpSession session, Long bookId) {
        GuestCart guestCart = (GuestCart) session.getAttribute("guestCart");
        if (guestCart == null) {
            throw new CartNotFoundException(session.getId());
        }

        boolean removed = guestCart.getItems().removeIf(item -> item.getBookId().equals(bookId));

        if (!removed) {
            throw CartItemNotFoundException.forBookId(bookId);
        }

        session.setAttribute("guestCart", guestCart);
    }

    @Transactional
    public void deleteAllGuestCartItems(HttpSession session) {
        GuestCart guestCart = (GuestCart) session.getAttribute("guestCart");
        guestCart.getItems().clear();
        session.setAttribute("guestCart", guestCart);
    }
}
