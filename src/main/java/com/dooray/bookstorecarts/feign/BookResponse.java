package com.dooray.bookstorecarts.feign;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class BookResponse {
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publicationDate;
    private Integer pageCount; // 추가: 페이지 수
    private String imageUrl; // 추가: 이미지 URL
    private String description;
    private Double originalPrice;
    private Double sellingPrice;
    private Double discountRate; // 할인율은 계산된 값
    private Boolean giftWrappingAvailable;
    private Integer likes;
    private List<List<CategoryResponse>> categories;
    private Set<String> tags;
}
