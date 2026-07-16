package org.example.frusitshopapp.service;

import lombok.RequiredArgsConstructor;
import org.example.frusitshopapp.dto.CartItemRequestDto;
import org.example.frusitshopapp.dto.CartItemResponseDto;
import org.example.frusitshopapp.entity.Cart;
import org.example.frusitshopapp.entity.CartItem;
import org.example.frusitshopapp.entity.Product;
import org.example.frusitshopapp.entity.User;
import org.example.frusitshopapp.exception.CustomException;
import org.example.frusitshopapp.exception.ErrorCode;
import org.example.frusitshopapp.repository.CartItemRepository;
import org.example.frusitshopapp.repository.CartRepository;
import org.example.frusitshopapp.repository.ProductRepository;
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

    //      1. 장바구니에 담기
    @Transactional
    public CartItemResponseDto addCart(CartItemRequestDto dto, User user) {

//      1-1. 유저로 Cart 찾기 (없으면 새로 만들기)
        Cart cart = cartRepository.findByUser(user).orElseGet(() ->
                cartRepository.save(Cart.builder().user(user).build()));

//      1-2. productId로 Product 찾기
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

//      1-3. CartItem에 이미 있으면 수량만 올리기
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + dto.getQuantity());
            return CartItemResponseDto.from(existingItem.get());
        }

//      1-4. 없으면 새 CartItem 만들어서 Cart에 추가
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(dto.getQuantity())
                .build();

        return CartItemResponseDto.from(cartItemRepository.save(cartItem));
    }

    //    2. 장바구니 조회 -- 유저로 카트찾고 아이템으로 반환?
    public List<CartItemResponseDto> getCartItem(User user) {

        Cart cart = cartRepository.findByUser(user).orElseThrow(()->
                new CustomException(ErrorCode.CART_NOT_FOUND));

        return cart.getCartItems().stream()
                .map(CartItemResponseDto::from)
                .toList();
    }

//    3. 장바구니 수정 -- 아이디로 찾고 확인후 변경
    @Transactional
    public CartItemResponseDto updateCartItem(Long cartItemId, CartItemRequestDto dto, User user){

        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(()->
                new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (!cartItem.getCart().getUser().getId().equals(user.getId())){
            throw new CustomException(ErrorCode.CART_FORBIDDEN);
        }
        cartItem.setQuantity(dto.getQuantity());
        return CartItemResponseDto.from(cartItem);
    }


//    4. 장바구니 삭제 -- 찾고 확인후 삭제
    @Transactional
    public void deleteCartItem(Long cartItemId,User user){
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(()->
                new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (!cartItem.getCart().getUser().getId().equals(user.getId())){
            throw new CustomException(ErrorCode.CART_FORBIDDEN);
        }
        cartItemRepository.delete(cartItem);
    }

}
