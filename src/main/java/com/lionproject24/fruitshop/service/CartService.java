package com.lionproject24.fruitshop.service;

import com.lionproject24.fruitshop.dto.CartItemRequestDto;
import com.lionproject24.fruitshop.dto.CartItemResponseDto;
import com.lionproject24.fruitshop.entity.Cart;
import com.lionproject24.fruitshop.entity.CartItem;
import com.lionproject24.fruitshop.entity.Product;
import com.lionproject24.fruitshop.entity.User;
import com.lionproject24.fruitshop.exception.CustomException;
import com.lionproject24.fruitshop.exception.ErrorCode;
import com.lionproject24.fruitshop.repository.CartItemRepository;
import com.lionproject24.fruitshop.repository.CartRepository;
import com.lionproject24.fruitshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    // 1. 장바구니에 담기
    @Transactional
    public CartItemResponseDto addCart(CartItemRequestDto dto, User user) {

        // 1-1. 유저로 Cart 찾기 (없으면 새로 만들기)
        Cart cart = getOrCreateCart(user);

        // 1-2. productId로 Product 찾기
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // 1-3. CartItem에 이미 있으면 수량만 올리기
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + dto.getQuantity());
            return CartItemResponseDto.from(existingItem.get());
        }

        // 1-4. 없으면 새 CartItem 만들어서 Cart에 추가
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(dto.getQuantity())
                .build();

        return CartItemResponseDto.from(cartItemRepository.save(cartItem));
    }

    // Cart 조회 또는 생성 - 동시 요청으로 인한 중복 생성을 예외처리로 방지
    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    try {
                        return cartRepository.save(Cart.builder().user(user).build());
                    } catch (DataIntegrityViolationException e) {
                        // 동시에 다른 요청이 먼저 Cart를 만든 경우 - 그걸 다시 찾아서 반환
                        return cartRepository.findByUser(user)
                                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));
                    }
                });
    }

    // 2. 장바구니 조회 - 유저로 카트 찾고 아이템 리스트 반환
    public List<CartItemResponseDto> getCartItem(User user) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));

        return cart.getCartItems().stream()
                .map(CartItemResponseDto::from)
                .toList();
    }

    // 3. 장바구니 수정 - id로 찾고 본인 확인 후 변경
    @Transactional
    public CartItemResponseDto updateCartItem(Long cartItemId, CartItemRequestDto dto, User user) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.CART_FORBIDDEN);
        }
        cartItem.setQuantity(dto.getQuantity());
        return CartItemResponseDto.from(cartItem);
    }

    // 4. 장바구니 삭제 - id로 찾고 본인 확인 후 삭제
    @Transactional
    public void deleteCartItem(Long cartItemId, User user) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.CART_FORBIDDEN);
        }
        cartItemRepository.delete(cartItem);
    }
}