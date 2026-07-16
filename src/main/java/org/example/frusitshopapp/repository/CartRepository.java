package org.example.frusitshopapp.repository;

import org.example.frusitshopapp.entity.Cart;
import org.example.frusitshopapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user); // -- 장바구니 조회

}
