package com.forest.microservices.core.recommendation.businesslayer;

import com.forest.api.core.recommendation.Recommendation;
import com.forest.api.core.review.Review;
import com.forest.microservices.core.recommendation.datalayer.RecommendationEntity;
import com.forest.microservices.core.recommendation.datalayer.RecommendationRepository;
import com.forest.utils.exceptions.NotFoundException;
import com.forest.utils.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    private final RecommendationRepository repository;

    private final RecommendationMapper mapper;

    private final ServiceUtil serviceUtil;

    public RecommendationServiceImpl(RecommendationRepository repository, RecommendationMapper mapper, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }
    @Override
    public List<Recommendation> getProductById(int productId){
            List<RecommendationEntity> entity = repository.findByProductId(productId);
            List<Recommendation> response = mapper.entityListToModelList(entity);
            response.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

            LOGGER.debug("Recommendations getProductId: response size: {}", response.size());
            return response;
    }


    @Override
    public Recommendation createRecommendation(Recommendation model) {
       RecommendationEntity entity = mapper.modelToEntity(model);
       RecommendationEntity newEntity = repository.save(entity);

        LOGGER.debug("RecommendationService createRecommendation: created a recommendation entity: {}/{}", model.getProductId(), model.getRecommendationId());
        return  mapper.entityToModel(newEntity);
    }

    @Override
    public void deleteRecommendations(int productId) {
        LOGGER.debug("deleteRecommendation: trying to delete all recommendations with productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId));
    }
}
