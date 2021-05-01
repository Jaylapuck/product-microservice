package com.forest.api.composite.product;

public class ServiceAddress {
    private String  compositeAddress;
    private String  productAddress;
    private String  reviewAddress;
    private String  recommendationAddress;

    public ServiceAddress(String compositeAddress, String productAddress, String reviewAddress, String recommendationAddress) {
        this.compositeAddress = compositeAddress;
        this.productAddress = productAddress;
        this.reviewAddress = reviewAddress;
        this.recommendationAddress = recommendationAddress;
    }

    public ServiceAddress() {
    }

    public String getCompositeAddress() {
        return this.compositeAddress;
    }

    public String getProductAddress() {
        return this.productAddress;
    }

    public String getReviewAddress() {
        return this.reviewAddress;
    }

    public String getRecommendationAddress() {
        return this.recommendationAddress;
    }

    public void setCompositeAddress(String compositeAddress) {
        this.compositeAddress = compositeAddress;
    }

    public void setProductAddress(String productAddress) {
        this.productAddress = productAddress;
    }

    public void setReviewAddress(String reviewAddress) {
        this.reviewAddress = reviewAddress;
    }

    public void setRecommendationAddress(String recommendationAddress) {
        this.recommendationAddress = recommendationAddress;
    }
}
