package com.example.demo.cart;

import com.example.demo.common.exception.InsufficientStockException;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.product.Product;
import com.example.demo.product.ProductRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Cart addProductToCart(User user, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ResourceNotFoundException.productById(productId));

        // Check stock availability
        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(productId, quantity, product.getStockQuantity());
        }

        // Check if product already in cart
        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            if (product.getStockQuantity() < newQuantity) {
                throw new InsufficientStockException(productId, newQuantity, product.getStockQuantity());
            }
            item.setQuantity(newQuantity);
        } else {
            // Add new item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getCartItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart updateItemQuantity(User user, Long productId, Integer quantity) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> ResourceNotFoundException.cartByUserId(user.getId()));

        if (quantity <= 0) {
            return removeProductFromCart(user, productId);
        }

        CartItem item = cart.getCartItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> ResourceNotFoundException.productById(productId));

        // Check stock
        Product product = item.getProduct();
        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(productId, quantity, product.getStockQuantity());
        }

        item.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    @Override
    public Cart removeProductFromCart(User user, Long productId) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> ResourceNotFoundException.cartByUserId(user.getId()));

        cart.getCartItems().removeIf(item -> item.getProduct().getId().equals(productId));

        return cartRepository.save(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public Cart getCart(User user) {
        return cartRepository.findByUserWithItems(user)
                .orElseGet(() -> getOrCreateCart(user));
    }

    @Override
    public void clearCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.userById(userId));

        cartRepository.findByUser(user).ifPresent(cart -> {
            cart.getCartItems().clear();
            cartRepository.save(cart);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cart> getCartByUserId(Long userId) {
        return userRepository.findById(userId)
                .flatMap(cartRepository::findByUserWithItems);
    }

    // --------------------------
    // Private Helper Methods
    // --------------------------
    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }
}