package com.forest.microservices.composite.product.getProduct.Integrationlayer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forest.api.core.product.Product;
import com.forest.api.core.product.ProductServiceAPI;
import com.forest.api.core.recommendation.Recommendation;
import com.forest.api.core.recommendation.RecommendationServiceAPI;
import com.forest.api.core.review.Review;
import com.forest.api.core.review.ReviewServiceAPI;
import com.forest.utils.exceptions.InvalidInputException;
import com.forest.utils.exceptions.NotFoundException;
import com.forest.utils.http.HttpErrorInfo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

@Component
public class ProductICompositeIntegration implements ProductServiceAPI, ReviewServiceAPI, RecommendationServiceAPI {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ProductICompositeIntegration.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String productServiceUrl;
    private final String recommendationServiceUrl;
    private final String reviewServiceUrl;

    public ProductICompositeIntegration(
            RestTemplate restTemplate,
            ObjectMapper mapper,

            @Value("${app.product-service.host}") String productServiceHost,
            @Value("${app.product-service.port}") int productServicePort,


            @Value("${app.recommendation-service.host}") String recommendationServiceHost,
            @Value("${app.recommendation-service.port}") int recommendationServicePort,

            @Value("${app.review-service.host}") String reviewServiceHost,
            @Value("${app.review-service.port}") int reviewServicePort
    ) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product/";
        recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation";
        reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review/";

        /*
        productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product/";
        recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation?productId=";
        reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review?productId=";
         */
    }

    private  RuntimeException handleHttpClientException(HttpClientErrorException ex){
        switch (ex.getStatusCode()){
            case NOT_FOUND:
                throw new NotFoundException(getErrorMessage(ex));
            case UNPROCESSABLE_ENTITY:
                throw new InvalidInputException(getErrorMessage(ex));
            default:
                LOGGER.warn("Got an unexpected HTTP error: {}, will rethrow it.", ex.getStatusCode());
                LOGGER.warn("Error body: {} ", ex.getResponseBodyAsString());
                throw ex;
        }
    }

    @Override
    public Product getProduct(int productId) {

        try {
           String url = productServiceUrl + productId ;
           LOGGER.debug("Will call getProduct API on URL: {}", url);
           Product product = restTemplate.getForObject(url, Product.class);
           LOGGER.debug("Found a product with ID: {}", productId);
           return product;
        }
        catch(HttpClientErrorException ex) { //We are the API client, so we need to handle errors {
            throw  handleHttpClientException(ex);
            }
        }

    @Override
    public Product createProduct(Product model) { //send POST request to the product service
        try {
            String url = productServiceUrl;
            LOGGER.debug("Will call createProduct API on url {}", url);
            return restTemplate.postForObject(productServiceUrl, model, Product.class);
        }
        catch (HttpClientErrorException ex){
            throw  handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteProduct(int productId) {
        try {
            String url = productServiceUrl + productId;
            LOGGER.debug("Will call deleteProduct API on url {}", url);
            restTemplate.delete(url);
        }
        catch (HttpClientErrorException ex){
            throw  handleHttpClientException(ex);
        }
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {

        try {
            String url = recommendationServiceUrl  + "?productId=" + productId;
            LOGGER.debug("Will call getRecommendation API on URL: {}", url);
            List<Recommendation> recommendation = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null, new ParameterizedTypeReference<List<Recommendation>>() {
                    }).getBody();

            LOGGER.debug("Found {} for a product with ID: {}", recommendation.size(), productId);
            return  recommendation;

        }
        catch (Exception ex) {
        LOGGER.debug("Got an exception while requesting recommendations, return zero recommendations: {}", ex.getMessage());
        return  new ArrayList<>();
        }
    }

    @Override
    public Recommendation createRecommendation(Recommendation model) {
        try {
            LOGGER.debug("Will call createRecommendation API on URL: {}", recommendationServiceUrl);
            return restTemplate.postForObject(recommendationServiceUrl, model, Recommendation.class);
        }
        catch (HttpClientErrorException ex){
            throw  handleHttpClientException(ex);
        }

    }

    @Override
    public void deleteRecommendations(int productId) {
        try {
            String url = recommendationServiceUrl  + "?productId=" + productId;
            LOGGER.debug("Will call deleteRecommendations API on url {}", url);
            restTemplate.delete(url);
        }
        catch (HttpClientErrorException ex){
            throw  handleHttpClientException(ex);
        }
    }

    @Override
    public List<Review> getReview(int productId) {
        try {
            String url = reviewServiceUrl + "?productId=" + productId;
            LOGGER.debug("Will call getReview API on URL: {}", url);
            List<Review> Review = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null, new ParameterizedTypeReference<List<Review>>() {
                    }).getBody();

            LOGGER.debug("Found {} for a product with ID: {}", Review.size(), productId);
            return  Review;

        }
        catch (Exception ex) {
            LOGGER.debug("Got an exception while requesting review, return zero review: {}", ex.getMessage());
            return  new ArrayList<>();
        }
    }

    @Override
    public Review createReview(Review model) {
        try {
            LOGGER.debug("Will call createReview API on URL: {}", reviewServiceUrl);
            return restTemplate.postForObject(reviewServiceUrl, model, Review.class);
        }
        catch (HttpClientErrorException ex){
            throw  handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteReviews(int productId) {
        try {
            String url = reviewServiceUrl  + "?productId=" + productId;
            LOGGER.debug("Will call deleteReviews API on url {}", url);
            restTemplate.delete(url);
        }
        catch (HttpClientErrorException ex){
            throw  handleHttpClientException(ex);
        }
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        }
        catch(IOException ioException) {
            return ioException.getMessage();
        }
    }


}
