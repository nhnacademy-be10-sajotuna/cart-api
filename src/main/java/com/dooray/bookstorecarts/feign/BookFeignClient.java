package com.dooray.bookstorecarts.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "BOOK-API")
public interface BookFeignClient {
    @GetMapping("/api/books/{bookId}")
    BookResponse getBook(@PathVariable("bookId") String bookId);

    @GetMapping("/api/books/batch")
    List<BookSummaryResponse> getBooksByIsbns(@RequestBody BookBatchRequest request);
}
