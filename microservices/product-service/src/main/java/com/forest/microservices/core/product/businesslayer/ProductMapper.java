package com.forest.microservices.core.product.businesslayer;

import com.forest.api.core.product.Product;
import com.forest.microservices.core.product.datalayer.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "serviceAddress", ignore=true)
    Product entityToModel(ProductEntity entity);
    @Mappings({
            @Mapping(target= "id", ignore = true),
            @Mapping(target="version", ignore = true)
    })
    ProductEntity modelToEntity(Product model);
}
