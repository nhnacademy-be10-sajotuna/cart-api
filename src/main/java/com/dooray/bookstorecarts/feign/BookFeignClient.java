package com.dooray.bookstorecarts.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "BOOK-API")
public interface BookFeignClient {
    @GetMapping("/api/books/{bookId}")
    BookResponse getBook(@PathVariable("bookId") String bookId);
}
