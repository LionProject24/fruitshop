package com.lionproject24.fruitshop.repository;

import com.lionproject24.fruitshop.entity.Cart;
import com.lionproject24.fruitshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user); // -- 장바구니 조회

}
