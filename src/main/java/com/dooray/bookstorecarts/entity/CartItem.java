package com.dooray.bookstorecarts.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cart_item")
@NoArgsConstructor
@Getter
@Setter
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_Id")
    private Long id;
    @Column(name = "book_id")
    private Long bookId;
    @Column(name = "quantity")
    private Long quantity;
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
}
