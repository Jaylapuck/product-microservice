package com.forest.microservices.core.review.businesslayer;

import com.forest.api.core.recommendation.Recommendation;
import com.forest.api.core.review.Review;
import com.forest.microservices.core.review.datalayer.ReviewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "serviceAddress", ignore = true)
    Review entitytoModel(ReviewEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    ReviewEntity modelToEntity(Review model);

    List<Review> entityListToModelList(List<ReviewEntity> entity);
    List<ReviewEntity> modelListToEntityList(List<Review> model);
}


