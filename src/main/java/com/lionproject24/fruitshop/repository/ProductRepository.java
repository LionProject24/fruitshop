package com.lionproject24.fruitshop.repository;

import com.lionproject24.fruitshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

//    연관된 이름도 찾기
    List<Product> findByNameContaining(String name);
}
