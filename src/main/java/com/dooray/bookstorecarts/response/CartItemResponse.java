package com.dooray.bookstorecarts.response;

import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.feign.BookResponse;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartItemDto;
import lombok.Data;

@Data
public class CartItemResponse {
    private Long cartItemId;
    private String bookId;
    private Long quantity;
    // book api 추가로 받아옴(장바구니 페이지에 필요)
    private String title;
    private String imageUrl;
    private Double originalPrice;
    private Double sellingPrice;


    public CartItemResponse(CartItem cartItem, BookResponse book) {
        this.cartItemId = cartItem.getId();
        this.bookId = cartItem.getBookId();
        this.quantity = cartItem.getQuantity();

        if(book != null) {
            this.title = book.getTitle();
            this.imageUrl = book.getImageUrl();
            this.originalPrice = book.getOriginalPrice();
            this.sellingPrice = book.getSellingPrice();
        }
    }

    public CartItemResponse(RedisGuestCartItemDto cartItem, BookResponse book) {
        this.cartItemId = null;
        this.bookId = cartItem.getBookId();
        this.quantity = cartItem.getQuantity();

        if(book != null) {
            this.title = book.getTitle();
            this.imageUrl = book.getImageUrl();
            this.originalPrice = book.getOriginalPrice();
            this.sellingPrice = book.getSellingPrice();
        }
    }
}
