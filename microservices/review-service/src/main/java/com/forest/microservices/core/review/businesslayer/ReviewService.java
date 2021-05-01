package com.forest.microservices.core.review.businesslayer;

import com.forest.api.core.product.Product;
import com.forest.api.core.recommendation.Recommendation;
import com.forest.api.core.review.Review;

import java.util.List;

public interface ReviewService {
    public List<Review> getReviews(int productId);

    public Review createReview(Review model);

    public void deleteReviews(int productId);
}
