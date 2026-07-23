package com.lionproject24.fruitshop.dto;

import com.lionproject24.fruitshop.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProductResponseDto {

    private Long id;
    private String name;
    private Long price;
    private Long stock;
    private String description;
    private String imageUrl;
    private String username;

    public static ProductResponseDto from(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .username(product.getUser().getUsername())
                .build();
    }
}
