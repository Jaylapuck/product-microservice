package com.forest.microservices.composite.product;

import com.forest.api.composite.product.ProductAggregate;
import com.forest.api.composite.product.RecommendationSummary;
import com.forest.api.composite.product.ReviewSummary;
import com.forest.api.core.product.Product;
import com.forest.api.core.recommendation.Recommendation;
import com.forest.api.core.review.Review;
import com.forest.microservices.composite.product.getProduct.Integrationlayer.ProductICompositeIntegration;
import com.forest.utils.exceptions.InvalidInputException;
import com.forest.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static reactor.core.publisher.Mono.just;
import static reactor.core.publisher.Mono.just;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
class ProductCompositeServiceApplicationTests {

	private static final int PRODUCT_ID_OKAY = 1;
	private static final int PRODUCT_NOT_FOUND = 213;
	private static final String PRODUCT_ID_INVALID_STRING = "not-integer";
	private static final int PRODUCT_ID_INVALID_NEGATIVE_VALUE = -1;

	@Autowired
	private WebTestClient client;

	@MockBean
	private ProductICompositeIntegration compositeIntegration;

	@BeforeEach
	void setup(){
		when(compositeIntegration.getProduct(PRODUCT_ID_OKAY)).
				thenReturn(new Product(PRODUCT_ID_OKAY, "name", 1,"mock-address"));

		when(compositeIntegration.getRecommendations(PRODUCT_ID_OKAY)).
				thenReturn(Collections.singletonList(new Recommendation(PRODUCT_ID_OKAY, 1, "author 1", 1, "content 1", "mock-address")));

		when(compositeIntegration.getReview(PRODUCT_ID_OKAY)).
				thenReturn(Collections.singletonList(new Review(PRODUCT_ID_OKAY, 1, "author 1", "subject 1", "content 1", "mock-address")));

		when(compositeIntegration.getProduct(PRODUCT_NOT_FOUND)).
				thenThrow(new NotFoundException("NOT FOUND: " + PRODUCT_NOT_FOUND));

		when(compositeIntegration.getProduct(PRODUCT_ID_INVALID_NEGATIVE_VALUE)).
				thenThrow(new InvalidInputException("INVALID INPUT: " + PRODUCT_ID_INVALID_NEGATIVE_VALUE));
	}


	@Test
	public void getProductById(){
		client.get()
				.uri("/product-composite/" + PRODUCT_ID_OKAY)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.productId").isEqualTo(PRODUCT_ID_OKAY)
				.jsonPath("$.recommendation.length()").isEqualTo(1)
				.jsonPath("$.review.length()").isEqualTo(1);
	}
	@Test
	public void createCompositeProductNoRecommendationsNoReviews(){

		ProductAggregate compositeProduct = new ProductAggregate(PRODUCT_ID_OKAY, "name 1", 1, null, null, null);

		client.post()
				.uri("/product-composite/")
				.body(just(compositeProduct),ProductAggregate.class)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();

	}

	@Test
	public void createCompositeProductOneRecommendationOneReview(){
		ProductAggregate compositeProduct = new ProductAggregate(PRODUCT_ID_OKAY, "name 1", 1,
				Collections.singletonList(new RecommendationSummary(1, "a", 1, "c")),
				Collections.singletonList(new ReviewSummary(1, "a", "s", "c")),
				null);

		client.post()
				.uri("/product-composite/")
				.body(just(compositeProduct),ProductAggregate.class)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	public void deleteCompositeProduct(){
		ProductAggregate compositeProduct = new ProductAggregate(PRODUCT_ID_OKAY, "name 1", 1,
				Collections.singletonList(new RecommendationSummary(1, "a", 1, "c")),
				Collections.singletonList(new ReviewSummary(1, "a", "s", "c")),
				null);

		client.post()
				.uri("/product-composite/")
				.body(just(compositeProduct),ProductAggregate.class)
				.exchange()
				.expectStatus().isOk();

		client.delete()
				.uri("/product-composite/" + compositeProduct.getProductId())
				.exchange()
				.expectStatus().isOk();

		client.delete()
				.uri("/product-composite/" + compositeProduct.getProductId())
				.exchange()
				.expectStatus().isOk();
	}
	@Test
	public void getProductNotFound(){
		client.get()
				.uri("/product-composite/" + PRODUCT_NOT_FOUND)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isNotFound()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_NOT_FOUND)
				.jsonPath("$.message").isEqualTo("NOT FOUND: " + PRODUCT_NOT_FOUND);

	}
	@Test
	public void getProductInvalidInput(){
		client.get()
				.uri("/product-composite/" + PRODUCT_ID_INVALID_STRING)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_INVALID_STRING)
				.jsonPath("$.message").isEqualTo("Type mismatch.");

	}

	@Test
	public void getProductNegativeValue(){
		client.get()
				.uri("/product-composite/" + PRODUCT_ID_INVALID_NEGATIVE_VALUE)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_INVALID_NEGATIVE_VALUE)
				.jsonPath("$.message").isEqualTo("INVALID INPUT: " + PRODUCT_ID_INVALID_NEGATIVE_VALUE);

	}
	@Test
	void contextLoads() {
	}

}
