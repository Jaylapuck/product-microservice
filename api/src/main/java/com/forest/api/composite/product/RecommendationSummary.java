package com.forest.api.composite.product;

public class RecommendationSummary {
    private int recommendationId;
    private String author;
    private int rate;
    private String content;

    public RecommendationSummary() {
    }

    public RecommendationSummary(int recommendationId, String author, int rate, String content) {
        this.recommendationId = recommendationId;
        this.author = author;
        this.rate = rate;
        this.content = content;
    }

    public int getRecommendationId() {
        return this.recommendationId;
    }

    public String getAuthor() {
        return this.author;
    }

    public int getRate() {
        return this.rate;
    }

    public String getContent() { return this.content; }

    public void setRecommendationId(int recommendationId) {
        this.recommendationId = recommendationId;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
