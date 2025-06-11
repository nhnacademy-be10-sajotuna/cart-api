package com.dooray.bookstorecarts.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartRequest {
    private Integer userId;
    private String sessionId;
}
