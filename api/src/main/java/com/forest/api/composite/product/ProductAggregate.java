package com.forest.api.composite.product;

import java.util.List;

public class ProductAggregate {
    private int productId;
    private String name;
    private int weight;
    private List<RecommendationSummary> recommendation;
    private List<ReviewSummary> review;
    private ServiceAddress serviceAddress;

    public ProductAggregate(int productId, String name, int weight, List<RecommendationSummary> recommendation, List<ReviewSummary> review, ServiceAddress serviceAddress) {
        this.productId = productId;
        this.name = name;
        this.weight = weight;
        this.recommendation = recommendation;
        this.review = review;
        this.serviceAddress = serviceAddress;
    }

    public ProductAggregate() {
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List<RecommendationSummary> getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(List<RecommendationSummary> recommendation) {
        this.recommendation = recommendation;
    }

    public List<ReviewSummary> getReview() {
        return review;
    }

    public void setReview(List<ReviewSummary> review) {
        this.review = review;
    }

    public ServiceAddress getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(ServiceAddress serviceAddress) {
        this.serviceAddress = serviceAddress;
    }
}


