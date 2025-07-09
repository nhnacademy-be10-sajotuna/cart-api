package com.dooray.bookstorecarts.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private String cartId;
    private List<CartItemResponse> items = new ArrayList<>();
}
