package com.forest.microservices.core.review.datalayer;

import com.forest.api.core.review.Review;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

public interface ReviewRepository extends CrudRepository<ReviewEntity, Integer> {

    @Transactional(readOnly = true)
    List<ReviewEntity> findByProductId(int productId);
}
