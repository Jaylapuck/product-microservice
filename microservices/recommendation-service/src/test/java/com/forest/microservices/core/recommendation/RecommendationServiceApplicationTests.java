package com.forest.microservices.core.recommendation;

import com.forest.api.core.product.Product;
import com.forest.api.core.recommendation.Recommendation;
import com.forest.microservices.core.recommendation.datalayer.RecommendationEntity;
import com.forest.microservices.core.recommendation.datalayer.RecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static reactor.core.publisher.Mono.just;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
class RecommendationServiceApplicationTests {

	private static final int PRODUCT_ID_OKAY = 1;
	private static final int RECOMMENDATION_IS_OKAY = 1;

	@Autowired
	private WebTestClient client;

	@Autowired
	private RecommendationRepository repository;

	@BeforeEach
	public void setupDb(){
		repository.deleteAll();
	}

	@Test
	public void getRecommendationByProductId() {

		RecommendationEntity entity1 = new RecommendationEntity(PRODUCT_ID_OKAY, 1, "author1", 1, "Content1");
		RecommendationEntity entity2 = new RecommendationEntity(PRODUCT_ID_OKAY, 2, "author2", 2, "Content2");
		RecommendationEntity entity3 = new RecommendationEntity(PRODUCT_ID_OKAY, 3, "author3", 3, "Content3");
		repository.save(entity1);
		repository.save(entity2);
		repository.save(entity3);

		assertEquals(3, repository.findByProductId(PRODUCT_ID_OKAY).size());

		int expectedLength = 3;
		client.get()
				.uri("/recommendation?productId=" + PRODUCT_ID_OKAY)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(expectedLength)
				.jsonPath("$[0].productId").isEqualTo(PRODUCT_ID_OKAY)
				.jsonPath("$[1].productId").isEqualTo(PRODUCT_ID_OKAY)
				.jsonPath("$[2].productId").isEqualTo(PRODUCT_ID_OKAY);

	}

	@Test
	public void createRecommendation(){

		//create an product model
		Recommendation model = new Recommendation(PRODUCT_ID_OKAY, RECOMMENDATION_IS_OKAY, "author1", 1, "content1", "SA");

		//send the post request
		client.post()
				.uri("/recommendation")
				.body(just(model), Recommendation.class)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.productId").isEqualTo(PRODUCT_ID_OKAY);

		assertEquals(1, repository.findByProductId(PRODUCT_ID_OKAY).size());
	}

	@Test
	public void deleteRecommendation(){
		RecommendationEntity entity = new RecommendationEntity(PRODUCT_ID_OKAY, RECOMMENDATION_IS_OKAY, "author1", 1, "content1");
		repository.save(entity);

		assertEquals(1, repository.findByProductId(PRODUCT_ID_OKAY).size());

		client.delete()
				.uri("/recommendation?productId=" + PRODUCT_ID_OKAY)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody();

		assertEquals(0, repository.findByProductId(PRODUCT_ID_OKAY).size());
	}

	@Test
	public void getRecommendationMissingParameter() {

		int expectedLength = 3;
		client.get()
				.uri("/recommendation")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/recommendation")
				.jsonPath("$.message").isEqualTo("Required int parameter 'productId' is not present");


	}

	@Test
	public void getRecommendationInvalidString() {

		int expectedLength = 3;
		String PRODUCT_ID_INVALID_STRING = "not-integer";
		client.get()
				.uri("/recommendation?productId=" + PRODUCT_ID_INVALID_STRING)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/recommendation")
				.jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	public void getRecommendationProductIdNotFound() {

		int PRODUCT_NOT_FOUND = 113;
		client.get()
				.uri("/recommendation?productId=" + PRODUCT_NOT_FOUND)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(0 );
	}

	@Test
	public void getRecommendationInvalidProductId() {

		int PRODUCT_ID_INVALID_NEGATIVE_VALUE = -1;
		client.get()
				.uri("/recommendation?productId=" + PRODUCT_ID_INVALID_NEGATIVE_VALUE)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/recommendation")
				.jsonPath("$.message").isEqualTo("Invalid productId: " +PRODUCT_ID_INVALID_NEGATIVE_VALUE );
	}

	@Test
	void contextLoads() {
	}

}
