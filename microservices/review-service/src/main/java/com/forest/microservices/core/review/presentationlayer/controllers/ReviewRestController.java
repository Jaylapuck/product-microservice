package com.forest.microservices.core.review.presentationlayer.controllers;

import com.forest.api.core.recommendation.Recommendation;
import com.forest.api.core.review.Review;
import com.forest.api.core.review.ReviewServiceAPI;
import com.forest.microservices.core.review.businesslayer.ReviewService;
import com.forest.utils.exceptions.InvalidInputException;
import com.forest.utils.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ReviewRestController implements ReviewServiceAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewRestController.class);

    private final ServiceUtil serviceUtil;

    private final ReviewService reviewService;

    public ReviewRestController(ServiceUtil serviceUtil, ReviewService reviewService) {
        this.serviceUtil = serviceUtil;
        this.reviewService = reviewService;
    }

    @Override
    public List<Review> getReview(int productId) {

        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        /*
        if (productId == 213) {
            LOGGER.debug("No review found for productId: {}", productId);
            return new ArrayList<>();
        }
         */

        List<Review> listReview = reviewService.getReviews(productId);
        /*
        List<Review> listReview = new ArrayList<>();
        listReview.add(new Review(productId, 1, "Author 1", "Subject 1", "Content 1", serviceUtil.getServiceAddress()));
        listReview.add(new Review(productId, 2, "Author 2", "Subject 2", "Content 2", serviceUtil.getServiceAddress()));
        listReview.add(new Review(productId, 3, "Author 3", "Subject 3", "Content 3", serviceUtil.getServiceAddress()));
         */

        LOGGER.debug("/reviews found response size: {}", listReview.size());

        return listReview;
    }

    @Override
    public Review createReview(Review model) {
        Review review = reviewService.createReview(model);
        LOGGER.debug("REST Controller createReview: created a recommendation entity: {}{}", review.getProductId(), review.getReviewId());
        return  review;
    }

    @Override
    public void deleteReviews(int productId) {
        LOGGER.debug("REST Controller deleteReviews: tried to delete all entity: {}", productId);
        reviewService.deleteReviews(productId);
    }
}
