package com.lionproject24.fruitshop.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//수정할 때 quantity만 바뀌는 건데 기존 dto로 받을 시 productId와 quantity 둘다 요청해서 분리
public class CartItemUpdateRequestDto {

    @NotNull(message = "수량은 필수입니다.")
    @Positive(message = "수량은 0보다 커야 합니다.")
    private Long quantity;
}
