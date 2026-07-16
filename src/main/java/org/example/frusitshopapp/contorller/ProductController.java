package org.example.frusitshopapp.contorller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.frusitshopapp.dto.ProductRequestDto;
import org.example.frusitshopapp.dto.ProductResponseDto;
import org.example.frusitshopapp.security.CustomUserDetails;
import org.example.frusitshopapp.service.ProductService;
import org.springframework.data.domain.Page;
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

    //    1. 상품등록
    @PostMapping
    public ResponseEntity<ProductResponseDto> add(@Valid @RequestBody ProductRequestDto dto,
                                                  @AuthenticationPrincipal CustomUserDetails details) {
        return ResponseEntity.ok(productService.addProduct(dto, details.getUser()));
    }

    //    2. 전체조회
    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getList(
            @PageableDefault(size = 15, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(productService.getProductList(pageable));
    }

//    3-1. id로 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long id){
        return ResponseEntity.ok(productService.getProduct(id));
    }

//    3-2. 이름으로 조회
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> searchProduct(@RequestParam String name){
        return ResponseEntity.ok(productService.searchProduct(name));
    }

//    4. 수정
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long id,
                                                            @Valid @RequestBody ProductRequestDto dto,
                                                            @AuthenticationPrincipal CustomUserDetails details){
        return ResponseEntity.ok(productService.updateProduct(id,details.getUser(),dto));
    }

//    5. 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id,@AuthenticationPrincipal CustomUserDetails details){
        productService.deleteProduct(id,details.getUser());
        return ResponseEntity.ok("상품 삭제 완료.");
    }
}
