package org.example.frusitshopapp.repository;

import org.example.frusitshopapp.entity.Order;
import org.example.frusitshopapp.entity.OrderItem;
import org.example.frusitshopapp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);

}
