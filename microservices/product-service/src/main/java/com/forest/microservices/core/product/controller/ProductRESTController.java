package com.forest.microservices.core.product.controller;

import com.forest.api.core.product.Product;
import com.forest.api.core.product.ProductServiceAPI;
import com.forest.microservices.core.product.businesslayer.ProductService;
import com.forest.utils.exceptions.InvalidInputException;
import com.forest.utils.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRESTController implements ProductServiceAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductRESTController.class);

    private final ProductService productService;

    public ProductRESTController(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public Product getProduct(int productId) {

        LOGGER.debug("/product MS return the found product fort productID: " + productId);

        if(productId < 1) throw new InvalidInputException("invalid productID: " + productId);

        //if(productId == 13) throw new NotFoundException("No product found for productID:" + productId);

        Product product = productService.getProductById(productId);

        return product;

    }

    @Override
    public Product createProduct(Product model) {
        Product product = productService.createProduct(model);
        LOGGER.debug("REST createdProduct: request sent to product service for productId: {} ", product.getProductId());
        return  product;
    }

    @Override
    public void deleteProduct(int productId) {
        LOGGER.debug("REST deletedProduct: tried to delete productId: {} ", productId);
        productService.deleteProduct(productId);
    }


}
