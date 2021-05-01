package com.forest.microservices.core.review;

import com.forest.api.core.review.Review;
import com.forest.microservices.core.review.datalayer.ReviewEntity;
import com.forest.microservices.core.review.datalayer.ReviewRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static reactor.core.publisher.Mono.just;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
class ReviewServiceApplicationTests {

	private static final int PRODUCT_ID_OKAY = 1;
	private static final int REVIEW_IS_OKAY = 1;

	@Autowired
	private WebTestClient client;

	@Autowired
	private ReviewRepository repository;

	@BeforeEach
	public void setupDb(){
		repository.deleteAll();
	}

	@Test
	public void getReviewByProductId() {

		ReviewEntity entity1 = new ReviewEntity(PRODUCT_ID_OKAY, 1,"author1", "subject1", "content1");
		ReviewEntity entity2 = new ReviewEntity(PRODUCT_ID_OKAY, 2,"author2", "subject2", "content2");
		ReviewEntity entity3 = new ReviewEntity(PRODUCT_ID_OKAY, 3,"author3", "subject3", "content3");
		repository.save(entity1);
		repository.save(entity2);
		repository.save(entity3);

		assertEquals(3, repository.findByProductId(PRODUCT_ID_OKAY).size());

		int expectedLength = 3;
		client.get()
				.uri("/review?productId=" + PRODUCT_ID_OKAY)
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
	public void createReview(){

		//create an review model
		Review model = new Review(PRODUCT_ID_OKAY, 1,"author1", "subject1", "content1", "SA");

		//send the post request
		client.post()
				.uri("/review")
				.body(just(model), Review.class)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.productId").isEqualTo(PRODUCT_ID_OKAY);

		assertEquals(1, repository.findByProductId(PRODUCT_ID_OKAY).size());
	}

	@Test
	public void deleteReview(){
		ReviewEntity entity = new ReviewEntity(PRODUCT_ID_OKAY, REVIEW_IS_OKAY , "author1", "subject1", "content1");
		repository.save(entity);

		assertEquals(1, repository.findByProductId(PRODUCT_ID_OKAY).size());

		client.delete()
				.uri("/review?productId=" + PRODUCT_ID_OKAY)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody();

		assertEquals(0, repository.findByProductId(PRODUCT_ID_OKAY).size());
	}

	@Test
	public void getReviewMissingParameter() {

		int expectedLength = 3;
		client.get()
				.uri("/review")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/review")
				.jsonPath("$.message").isEqualTo("Required int parameter 'productId' is not present");


	}

	@Test
	public void getReviewInvalidString() {

		int expectedLength = 3;
		String PRODUCT_ID_INVALID_STRING = "not-integer";

		client.get()
				.uri("/review?productId=" + PRODUCT_ID_INVALID_STRING)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/review")
				.jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	public void getReviewProductIdNotFound() {

		int PRODUCT_NOT_FOUND = 213;
		int expectedLength = 0;
		client.get()
				.uri("/review?productId=" + PRODUCT_NOT_FOUND)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(expectedLength);
	}

	@Test
	public void getReviewNegativeProductId() {

		int PRODUCT_ID_INVALID_NEGATIVE_VALUE = -1;

		client.get()
				.uri("/review?productId=" + PRODUCT_ID_INVALID_NEGATIVE_VALUE)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/review")
				.jsonPath("$.message").isEqualTo("Invalid productId: " + PRODUCT_ID_INVALID_NEGATIVE_VALUE);
	}
	@Test
	void contextLoads() {
	}
}