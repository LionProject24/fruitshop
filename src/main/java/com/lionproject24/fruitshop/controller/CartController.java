package com.lionproject24.fruitshop.controller;

import com.lionproject24.fruitshop.dto.CartItemRequestDto;
import com.lionproject24.fruitshop.dto.CartItemResponseDto;
import com.lionproject24.fruitshop.dto.CartItemUpdateRequestDto;
import com.lionproject24.fruitshop.security.CustomUserDetails;
import com.lionproject24.fruitshop.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    //    1. 장바구니 추가
    @Operation(summary = "장바구니 추가")
    @PostMapping
    public ResponseEntity<CartItemResponseDto> addCart(@Valid @RequestBody CartItemRequestDto dto,
                                                       @AuthenticationPrincipal CustomUserDetails details) {
        return ResponseEntity.ok(cartService.addCart(dto, details.getUser()));
    }

    //    2. 장바구니 조회
    @Operation(summary = "장바구니 조회")
    @GetMapping
    public ResponseEntity<List<CartItemResponseDto>> getCartItem(@AuthenticationPrincipal CustomUserDetails details) {
        return ResponseEntity.ok(cartService.getCartItem(details.getUser()));
    }

    //    3. 장바구니 변경
    @Operation(summary = "장바구니 변경")
    @PutMapping("/{id}")
    public ResponseEntity<CartItemResponseDto> updateCartItem(@PathVariable Long id,
                                                              @Valid @RequestBody CartItemUpdateRequestDto dto,
                                                              @AuthenticationPrincipal CustomUserDetails details) {
        return ResponseEntity.ok(cartService.updateCartItem(id, dto, details.getUser()));
    }

    //    4. 장바구니 삭제
    @Operation(summary = "장바구니 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails details) {
        cartService.deleteCartItem(id, details.getUser());

        return ResponseEntity.ok("장바구니 삭제 완료.");

    }


}
