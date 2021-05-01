package com.forest.microservices.composite.product.getProduct.businesslayer;

import com.forest.api.composite.product.ProductAggregate;

public interface ProductCompostiteService {

    public ProductAggregate getProduct(int productId);

    public void createProduct(ProductAggregate model);

    public void deleteProduct(int productId);
}
