package com.dooray.bookstorecarts.response;

import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.feign.BookResponse;
import com.dooray.bookstorecarts.feign.BookSummaryResponse;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartItemDto;
import lombok.Data;

import java.util.List;

@Data
public class CartItemResponse {
    private Long cartItemId;
    private String isbn;
    private Long quantity;
    // book api 추가로 받아옴(장바구니 페이지에 필요)
    private String title;
    private String imageUrl;
    private Double originalPrice;
    private Double sellingPrice;
    private List<Long> categoryIds;

    public CartItemResponse(CartItem cartItem, BookSummaryResponse book) {
        this.cartItemId = cartItem.getId();
        this.isbn = cartItem.getIsbn();
        this.quantity = cartItem.getQuantity();

        if(book != null) {
            this.title = book.getTitle();
            this.imageUrl = book.getImageUrl();
            this.originalPrice = book.getOriginalPrice();
            this.sellingPrice = book.getSellingPrice();
            this.categoryIds = book.getCategoryIds();
        }
    }

    public CartItemResponse(RedisGuestCartItemDto cartItem, BookSummaryResponse book) {
        this.cartItemId = null;
        this.isbn = cartItem.getIsbn();
        this.quantity = cartItem.getQuantity();

        if(book != null) {
            this.title = book.getTitle();
            this.imageUrl = book.getImageUrl();
            this.originalPrice = book.getOriginalPrice();
            this.sellingPrice = book.getSellingPrice();
            this.categoryIds = book.getCategoryIds();
        }
    }

    public CartItemResponse(RedisGuestCartItemDto cartItem, BookResponse book) {
        this.cartItemId = null;
        this.isbn = cartItem.getIsbn();
        this.quantity = cartItem.getQuantity();

        if(book != null) {
            this.title = book.getTitle();
            this.imageUrl = book.getImageUrl();
            this.originalPrice = book.getOriginalPrice();
            this.sellingPrice = book.getSellingPrice();
        }
    }
}
