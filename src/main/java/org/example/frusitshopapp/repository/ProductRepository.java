package org.example.frusitshopapp.repository;

import org.example.frusitshopapp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
// 관련이름으로 찾기  -- 바나나
    List<Product> findByNameContaining(String name);
}
