package org.example.frusitshopapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.frusitshopapp.entity.CartItem;

@Getter
@AllArgsConstructor
@Builder
public class CartItemResponseDto {

    private Long productId;
    private String productName;
    private Long quantity;
    private Long price;

    public static CartItemResponseDto from(CartItem cartItem){
        return CartItemResponseDto.builder()
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .price(cartItem.getProduct().getPrice())
                .quantity(cartItem.getQuantity())
                .build();
    }
}
