package com.lionproject24.fruitshop.controller;

import com.lionproject24.fruitshop.dto.ProductRequestDto;
import com.lionproject24.fruitshop.dto.ProductResponseDto;
import com.lionproject24.fruitshop.security.CustomUserDetails;
import com.lionproject24.fruitshop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    // 1. 상품 등록
    @PostMapping
    public ResponseEntity<ProductResponseDto> add(@Valid @RequestBody ProductRequestDto dto,
                                                  @AuthenticationPrincipal CustomUserDetails details) {
        return ResponseEntity.ok(productService.addProduct(dto, details.getUser()));
    }

    // 2. 전체조회 -- swagger 테스트에서 sort 때문에 계속 오류나서 따로따로 파라미터로 받게함
    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return ResponseEntity.ok(productService.getProductList(pageable));
    }

    // 3-1. id로 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    // 3-2. 이름으로 조회
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> searchProduct(@RequestParam String name) {
        return ResponseEntity.ok(productService.searchProduct(name));
    }

    // 4. 수정
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long id,
                                                            @Valid @RequestBody ProductRequestDto dto,
                                                            @AuthenticationPrincipal CustomUserDetails details) {
        return ResponseEntity.ok(productService.updateProduct(id, details.getUser(), dto));
    }

    // 5. 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id,
                                         @AuthenticationPrincipal CustomUserDetails details) {
        productService.deleteProduct(id, details.getUser());
        return ResponseEntity.ok("상품 삭제 완료.");
    }
}