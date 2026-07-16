package org.example.frusitshopapp.repository;

import org.example.frusitshopapp.entity.Cart;
import org.example.frusitshopapp.entity.CartItem;
import org.example.frusitshopapp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
//    이미 담긴 상품 다시담을때 수량만 올리기위함
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
