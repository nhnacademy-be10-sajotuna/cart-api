package com.dooray.bookstorecarts.redisdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestCart {
    private String sessionId;
    private List<GuestCartItem> items;
}
