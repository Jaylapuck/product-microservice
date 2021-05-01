package com.forest.microservices.core.recommendation.businesslayer;

import com.forest.api.core.recommendation.Recommendation;
import com.forest.api.core.review.Review;

import java.util.List;

public interface RecommendationService {

    public List<Recommendation> getProductById(int productId);

    public Recommendation createRecommendation(Recommendation model);

    public void deleteRecommendations(int productId);
}
