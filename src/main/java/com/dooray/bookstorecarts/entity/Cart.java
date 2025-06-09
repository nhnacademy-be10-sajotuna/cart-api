package com.dooray.bookstorecarts.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "carts")
@NoArgsConstructor
@Getter
@Setter
public class Cart {
    private int id;
    private
}
