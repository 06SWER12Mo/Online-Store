package com.example.demo.review;

import com.example.demo.product.Product;
import com.example.demo.product.ProductRepository;
import com.example.demo.review.dtos.ReviewRequest;
import com.example.demo.review.dtos.ReviewResponse;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewMapper {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ReviewMapper(UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public Review toEntity(ReviewRequest request, Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return new Review(
                request.getRating(),
                request.getComment(),
                user,
                product
        );
    }

    public ReviewResponse toResponse(Review review) {
        return new ReviewResponse(review);
    }

    public List<ReviewResponse> toResponseList(List<Review> reviews) {
        return reviews.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}