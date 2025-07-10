package com.dooray.bookstorecarts.feign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookSummaryResponse {
    private String isbn;
    private String title;
    private String imageUrl;
    private Double originalPrice;
    private Double sellingPrice;
    private List<Long> categoryIds;
}