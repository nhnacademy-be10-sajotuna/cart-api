package com.dooray.bookstorecarts.feign;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class BookSummaryResponse {
    private String isbn;
    private String title;
    private String imageUrl;
    private Double originalPrice;
    private Double sellingPrice;
    private List<Long> categoryIds;
}