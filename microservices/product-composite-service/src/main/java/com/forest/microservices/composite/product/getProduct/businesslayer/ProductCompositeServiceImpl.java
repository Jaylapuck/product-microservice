package com.forest.microservices.composite.product.getProduct.businesslayer;

import com.forest.api.composite.product.ProductAggregate;
import com.forest.api.composite.product.RecommendationSummary;
import com.forest.api.composite.product.ReviewSummary;
import com.forest.api.composite.product.ServiceAddress;
import com.forest.api.core.product.Product;
import com.forest.api.core.recommendation.Recommendation;
import com.forest.api.core.review.Review;
import com.forest.microservices.composite.product.getProduct.Integrationlayer.ProductICompositeIntegration;
import com.forest.utils.exceptions.NotFoundException;
import com.forest.utils.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCompositeServiceImpl implements ProductCompostiteService{

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductCompositeServiceImpl.class);

    private final ProductICompositeIntegration integration;

    private final ServiceUtil serviceUtil;

    public ProductCompositeServiceImpl(ProductICompositeIntegration integration, ServiceUtil serviceUtil) {
        this.integration = integration;
        this.serviceUtil = serviceUtil;
    }


    @Override
    public ProductAggregate getProduct(int productId) {
        Product product = integration.getProduct(productId);
        if (product == null){ throw new NotFoundException("No product found for productId: " + productId); }

        List<Recommendation> recommendation = integration.getRecommendations(productId);

        List<Review> review = integration.getReview(productId);

        return createProductAggregate(product, recommendation, review, serviceUtil.getServiceAddress());
    }

    @Override
    public void createProduct(ProductAggregate model) {

        try {
            LOGGER.debug("createCompositeProduct: creates a new composite entity for productId: {}", model.getProductId());
            Product product = new Product(model.getProductId(), model.getName(), model.getWeight(), null);
            integration.createProduct(product);

            if (model.getRecommendation() != null){
                model.getRecommendation().forEach(r -> {
                    Recommendation recommendation = new Recommendation(model.getProductId(), r.getRecommendationId(),
                            r.getAuthor(), r.getRate(), r.getContent(), null);
                });
            }

            if (model.getReview() != null){
                model.getReview().forEach(r -> {
                    Review review = new Review(model.getProductId(), r.getReviewId(),
                            r.getAuthor(), r.getSubject(), r.getContent(), null);
                });
            }
            LOGGER.debug("createCompositeProduct: composite entities created for productId: {}", model.getProductId());
        } catch (RuntimeException rte) {
            LOGGER.warn("createCompositeProduct failed", rte);
        }
    }
    @Override
    public void deleteProduct(int productId) {
        LOGGER.debug("deleteCompositeProduct: starting to delete a product aggregate for productId: {}", productId);

        integration.deleteProduct(productId);
        integration.deleteRecommendations(productId);
        integration.deleteReviews(productId);

        LOGGER.debug("deleteCompositeProduct: Deleted a product aggregate for productId: {}", productId);
    }

    private ProductAggregate createProductAggregate(Product product, List<Recommendation> recommendation, List<Review> review, String serviceAddress) {
        //1. Setup the product info
        int productId = product.getProductId();
        String name = product.getName();
        int weight = product.getWeight();

        //2. Copy summary recommendation if, if available
        List<RecommendationSummary> recommendationSummaries = (recommendation == null) ? null :
                recommendation.stream().map(r -> new RecommendationSummary(r.getRecommendationId(), r.getAuthor(),
                        r.getRate(), r.getContent())).collect(Collectors.toList());

        //3. Copy summary review if, if available
        List<ReviewSummary> reviewSummaries = (review == null) ? null :
                review.stream().map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(),
                        r.getSubject(), r.getContent())).collect(Collectors.toList());

        //4. Create summary of Microservice addresses

        String productAddress = product.getServiceAddress();
        String recommendationAddress = (recommendation !=
                null && recommendation.size() > 0) ? recommendation.get(0).getServiceAddress() : "";
        String reviewAddress = (review !=
                null && review.size() > 0) ? review.get(0).getServiceAddress() : "";
        ServiceAddress serviceAddresses  = new ServiceAddress(serviceAddress, productAddress, reviewAddress, recommendationAddress);

        //5. return
        return new ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);
    }

}
