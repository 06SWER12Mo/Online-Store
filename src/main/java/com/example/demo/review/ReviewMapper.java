package com.example.demo.review;

import com.example.demo.product.Product;
import com.example.demo.product.ProductRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        Review review = new Review();
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setUser(user);
        review.setProduct(product);
        review.setReviewerName(user.getFirstName() + " " + user.getLastName());
        
        // Check if user has purchased this product (this would need OrderService integration)
        // For now, we'll set it to false by default
        review.setVerifiedPurchase(false);
        
        return review;
    }

    public void updateEntity(Review review, ReviewUpdateRequest request) {
        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }
    }

    public ReviewResponse toResponse(Review review) {
        return new ReviewResponse(review);
    }

    public List<ReviewResponse> toResponseList(List<Review> reviews) {
        return reviews.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ReviewImage toImageEntity(String imageUrl, Review review) {
        ReviewImage image = new ReviewImage();
        image.setImageUrl(imageUrl);
        image.setReview(review);
        image.setDisplayOrder(review.getImages().size());
        return image;
    }

    public ReviewImageResponse toImageResponse(ReviewImage image) {
        return new ReviewImageResponse(image);
    }

    public List<ReviewImageResponse> toImageResponseList(List<ReviewImage> images) {
        return images.stream()
                .map(this::toImageResponse)
                .collect(Collectors.toList());
    }
}