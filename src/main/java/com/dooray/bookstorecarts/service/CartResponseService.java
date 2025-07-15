package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.feign.BookBatchRequest;
import com.dooray.bookstorecarts.feign.BookFeignClient;
import com.dooray.bookstorecarts.feign.BookSummaryResponse;
import com.dooray.bookstorecarts.redisdto.RedisCartDto;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartDto;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartItemDto;
import com.dooray.bookstorecarts.response.CartItemResponse;
import com.dooray.bookstorecarts.response.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartResponseService {
    
    private final BookFeignClient bookFeignClient;
    
    public CartResponse createFromUserCart(RedisCartDto cart, List<CartItem> items) {
        if (items.isEmpty()) {
            return new CartResponse(String.valueOf(cart.getCartId()), new ArrayList<>());
        }
        
        List<String> isbns = items.stream()
                .map(CartItem::getIsbn)
                .toList();
        
        List<BookSummaryResponse> books = bookFeignClient.getBooksByIsbns(new BookBatchRequest(isbns));
        Map<String, BookSummaryResponse> bookMap = books.stream()
                .collect(Collectors.toMap(BookSummaryResponse::getIsbn, book -> book));
        
        List<CartItemResponse> cartItems = items.stream()
                .map(item -> new CartItemResponse(item, bookMap.get(item.getIsbn())))
                .toList();
        
        return new CartResponse(String.valueOf(cart.getCartId()), cartItems);
    }
    
    public CartResponse createFromGuestCart(RedisGuestCartDto guestCart) {
        if (guestCart.getItems().isEmpty()) {
            return new CartResponse(guestCart.getCartId(), new ArrayList<>());
        }
        
        List<String> isbns = guestCart.getItems().stream()
                .map(RedisGuestCartItemDto::getIsbn)
                .toList();
        
        List<BookSummaryResponse> books = bookFeignClient.getBooksByIsbns(new BookBatchRequest(isbns));
        Map<String, BookSummaryResponse> bookMap = books.stream()
                .collect(Collectors.toMap(BookSummaryResponse::getIsbn, book -> book));
        
        List<CartItemResponse> cartItems = guestCart.getItems().stream()
                .map(item -> new CartItemResponse(item, bookMap.get(item.getIsbn())))
                .toList();
        
        return new CartResponse(guestCart.getCartId(), cartItems);
    }
}