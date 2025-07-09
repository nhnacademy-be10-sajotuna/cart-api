package com.dooray.bookstorecarts.feign;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BookBatchRequest {
    private List<String> isbns;
}
