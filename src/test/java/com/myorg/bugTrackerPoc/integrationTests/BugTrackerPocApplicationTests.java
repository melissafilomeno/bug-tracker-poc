package com.myorg.bugTrackerPoc.integrationTests;

import com.myorg.bugTrackerPoc.entity.Bug;
import com.myorg.bugTrackerPoc.repository.BugRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
class BugTrackerPocApplicationTest {

	@LocalServerPort
	private Integer port;

	@Container
	@ServiceConnection
	static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:latest"))
			.withInitScript("db/db_schema.sql");

	@BeforeAll
	static void beforeAll(){
		mySQLContainer.start();
	}

	@AfterAll
	static void afterAll(){
		mySQLContainer.stop();
	}

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
		dynamicPropertyRegistry.add("spring.datasource.username", mySQLContainer::getUsername);
		dynamicPropertyRegistry.add("spring.datasource.password", mySQLContainer::getPassword);
	}

	@Autowired
	private BugRepository bugRepository;

	@BeforeEach
	void setUp(){
		RestAssured.baseURI = "http://localhost:" + port;
		bugRepository.deleteAll();
	}

	@Test
	void saveBug_success() {
		String description = "My first bug";
		given()
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.body("{\"description\": \"" + description + "\"}")
			.post("/bugs")
		.then()
			.statusCode(HttpStatus.CREATED.value())
			.body("description", is(description))
			.body("id", notNullValue());

		assertEquals(1L, bugRepository.count());
		Bug bugInDb = bugRepository.findAll().iterator().next();
		assertEquals(description, bugInDb.getDescription());
	}

	@Test
	void getAllBugs_success(){
		List<Bug> bugList = new ArrayList<>();
		bugList.add(new Bug("desc1"));
		bugList.add(new Bug("desc2"));
		bugRepository.saveAll(bugList);

		given()
			.get("/bugs")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body(".", hasSize(2));
	}

	@Test
	void getBugById_success(){
		String description = "My first bug";
		Bug bug = new Bug(description);
		Bug insertedBug = bugRepository.save(bug);

		given()
			.get("/bugs/" + insertedBug.getId())
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("description", is(description))
			.body("id", is(insertedBug.getId()));
	}

	@Test
	void getBugById_NotFound(){
		given()
			.get("/bugs/1")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("code", is(String.valueOf(HttpStatus.NOT_FOUND.value())))
			.body("message", is("Not Found"));
}
}