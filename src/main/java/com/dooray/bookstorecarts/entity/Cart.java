package com.dooray.bookstorecarts.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cart")
@NoArgsConstructor
@Getter
@Setter
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;
    private Long userId;

    public Cart(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }
}
