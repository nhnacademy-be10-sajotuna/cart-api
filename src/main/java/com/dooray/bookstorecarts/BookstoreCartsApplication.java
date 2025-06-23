package com.dooray.bookstorecarts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800)
public class BookstoreCartsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookstoreCartsApplication.class, args);
    }

}
