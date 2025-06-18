package com.dooray.bookstorecarts.redisdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestCart implements Serializable {
    private static final long serialVersionUID = 1L;
    private String sessionId;
    private List<GuestCartItem> items;
}
