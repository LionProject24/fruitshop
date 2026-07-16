package org.example.frusitshopapp.service;

import lombok.RequiredArgsConstructor;
import org.example.frusitshopapp.dto.ProductRequestDto;
import org.example.frusitshopapp.dto.ProductResponseDto;
import org.example.frusitshopapp.entity.Product;
import org.example.frusitshopapp.entity.User;
import org.example.frusitshopapp.exception.CustomException;
import org.example.frusitshopapp.exception.ErrorCode;
import org.example.frusitshopapp.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    //    1. 상품 등록 (판매자)
    @Transactional
    public ProductResponseDto addProduct(ProductRequestDto dto, User user) {
        Product product = Product.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .user(user)
                .build();

        Product saved = productRepository.save(product);
        return ProductResponseDto.from(saved);
    }

//    2. 상품 전체조회
    public Page<ProductResponseDto> getProductList(Pageable pageable){

        return productRepository.findAll(pageable)
                .map(ProductResponseDto::from);
    }

//    3-1. 상품 선택조회 -- id
    public ProductResponseDto getProduct(Long id){
        Product product = productRepository.findById(id).orElseThrow(()->
                new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        return ProductResponseDto.from(product);
    }

//    3-2. 상품 선택조회 -- 이름
    public List<ProductResponseDto> searchProduct(String name){
        return productRepository.findByNameContaining(name)
                .stream().map(ProductResponseDto::from).toList();
    }

//    4. 상품 수정(본인)
    @Transactional
    public ProductResponseDto updateProduct(Long id, User user, ProductRequestDto requestDto){
        Product product = productRepository.findById(id).orElseThrow(()->
                new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getUser().getId().equals(user.getId())){
            throw new CustomException(ErrorCode.PRODUCT_FORBIDDEN);
        }

        product.setName(requestDto.getName());
        product.setPrice(requestDto.getPrice());
        product.setStock(requestDto.getStock());
        product.setDescription(requestDto.getDescription());
        product.setImageUrl(requestDto.getImageUrl());

        return ProductResponseDto.from(product);
    }

//    5. 상품 삭제(본인)
    @Transactional
    public void deleteProduct(Long id, User user){
        Product product = productRepository.findById(id).orElseThrow(()->
                new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getUser().getId().equals(user.getId())){
            throw new CustomException(ErrorCode.PRODUCT_FORBIDDEN);
        }
        productRepository.delete(product);
    }
}
