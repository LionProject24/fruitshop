package com.lionproject24.fruitshop.dto;

import com.lionproject24.fruitshop.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CartItemResponseDto {

    private Long cartItemId;
    private Long productId;
    private String productName;
    private Long quantity;
    private Long price;

    public static CartItemResponseDto from(CartItem cartItem) {
        return CartItemResponseDto.builder()
                .cartItemId(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .price(cartItem.getProduct().getPrice())
                .quantity(cartItem.getQuantity())
                .build();
    }
}