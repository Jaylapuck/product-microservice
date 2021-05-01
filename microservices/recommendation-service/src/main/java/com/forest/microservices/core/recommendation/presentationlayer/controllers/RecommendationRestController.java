package com.forest.microservices.core.recommendation.presentationlayer.controllers;

import com.forest.api.core.product.Product;
import com.forest.api.core.recommendation.Recommendation;
import com.forest.api.core.recommendation.RecommendationServiceAPI;
import com.forest.microservices.core.recommendation.businesslayer.RecommendationService;
import com.forest.utils.exceptions.InvalidInputException;
import com.forest.utils.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RecommendationRestController implements RecommendationServiceAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendationRestController.class);

    private final ServiceUtil serviceUtil;

    private final RecommendationService recommendationService;

    public RecommendationRestController(ServiceUtil serviceUtil, RecommendationService recommendationService) {
        this.serviceUtil = serviceUtil;
        this.recommendationService = recommendationService;
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {

        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        /*if (productId == 113) {
            LOGGER.debug("No recommendations found for productId: {}", productId);
            return new ArrayList<>();
        }
         */

        List<Recommendation> recommendationsList = recommendationService.getProductById(productId);
        /*
        recommendationsList.add(new Recommendation(productId, 1, "author 1", 3 ,"content 1", serviceUtil.getServiceAddress()));
        recommendationsList.add(new Recommendation(productId, 2, "author 2", 4 ,"content 2", serviceUtil.getServiceAddress()));
        recommendationsList.add(new Recommendation(productId, 3, "author 3", 1 ,"content 3", serviceUtil.getServiceAddress()));
         */

        return recommendationsList;
    }

    @Override
    public Recommendation createRecommendation(Recommendation model){
        Recommendation recommendation = recommendationService.createRecommendation(model);
        LOGGER.debug("REST Controller createRecommendation: created a recommendation entity: {}{}", recommendation.getProductId(), recommendation.getRecommendationId());
        return  recommendation;
    }

    @Override
    public void deleteRecommendations(int productId){
        LOGGER.debug("REST Controller deleteRecommendations: tried to delete all entity: {}", productId);
        recommendationService.deleteRecommendations(productId);
    }
}
