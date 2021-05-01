package com.forest.microservices.core.product.businesslayer;

import com.forest.api.core.product.Product;
import com.forest.microservices.core.product.datalayer.ProductEntity;
import com.forest.microservices.core.product.datalayer.ProductRepository;
import com.forest.utils.exceptions.InvalidInputException;
import com.forest.utils.exceptions.NotFoundException;
import com.forest.utils.http.ServiceUtil;
import com.mongodb.DuplicateKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements  ProductService{

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository repository;

    private final ProductMapper mapper;

    private final ServiceUtil serviceUtil;

    public ProductServiceImpl(ProductRepository repository, ProductMapper mapper, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Product getProductById(int productId) {
       ProductEntity entity = repository.findByProductId(productId)
               .orElseThrow(() -> new NotFoundException("No product found for productID:" + productId));
       Product response = mapper.entityToModel(entity);
       response.setServiceAddress(serviceUtil.getServiceAddress());
       LOGGER.debug("Product getProductById: found productId: {}", response.getProductId());
       return response;
    }

    @Override
    public Product createProduct(Product model) {
        try {
            ProductEntity entity = mapper.modelToEntity(model);
            ProductEntity newEntity = repository.save(entity);
            LOGGER.debug("createProduct: entity created for productId: {}", model.getProductId());

            return mapper.entityToModel(newEntity);
        }
        catch (DuplicateKeyException e){
            throw new InvalidInputException("Duplicate key for productId " + model.getProductId());
        }
    }

    @Override
    public void deleteProduct(int productId) {
        LOGGER.debug("deleteProduct: trying to delete entity with productId: {}", productId);
        repository.findByProductId(productId).ifPresent(e -> repository.delete(e));
    }
}
