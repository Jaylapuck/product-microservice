package com.forest.microservices.composite.product.getProduct.controller;

import com.forest.api.composite.product.*;
import com.forest.api.core.product.Product;
import com.forest.api.core.recommendation.Recommendation;
import com.forest.api.core.review.Review;
import com.forest.microservices.composite.product.getProduct.Integrationlayer.ProductICompositeIntegration;
import com.forest.microservices.composite.product.getProduct.businesslayer.ProductCompostiteService;
import com.forest.utils.exceptions.NotFoundException;
import com.forest.utils.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ProductCompositeRestController implements ProductCompositeServiceAPI {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ProductCompositeRestController.class);
    private ProductCompostiteService productCompostiteService;


    public ProductCompositeRestController(ProductCompostiteService productCompostiteService) {
        this.productCompostiteService = productCompostiteService;
    }

    @Override
    public ProductAggregate getCompositeProduct(int productId) {
        LOGGER.debug("ProductComposite REST received getCompositeProduct request for productId: {}",productId);
        return productCompostiteService.getProduct(productId);

    }

    @Override
    public void createCompositeProduct(ProductAggregate model) {
        LOGGER.debug("ProductComposite REST received createCompositeProduct request for productId");
        productCompostiteService.createProduct(model);
    }

    @Override
    public void deleteCompositeProduct(int productId) {
        LOGGER.debug("ProductComposite REST received deleteCompositeProduct request for productId: {}",productId);
        productCompostiteService.deleteProduct(productId);
    }
}

