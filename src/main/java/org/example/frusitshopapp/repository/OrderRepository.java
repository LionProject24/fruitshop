package org.example.frusitshopapp.repository;

import org.example.frusitshopapp.entity.CartItem;
import org.example.frusitshopapp.entity.Order;
import org.example.frusitshopapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUser(User user);
}
