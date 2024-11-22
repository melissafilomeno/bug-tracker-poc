package com.myorg.bugTrackerPoc;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.ContentType;
import com.myorg.bugTrackerPoc.entity.Bug;
import com.myorg.bugTrackerPoc.repository.BugRepository;
import static org.junit.Assert.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import io.restassured.RestAssured;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.*;

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
				.header("Content-Type", "application/json")
				.body("{\"description\": \"" + description + "\"}")
				.post("/bugs")
				.then()
				.statusCode(201)
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
				.statusCode(200)
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
				.statusCode(200)
				.body("description", is(description))
				.body("id", is(insertedBug.getId()));
	}

	@Test
	void getBugById_NotFound(){
		given()
				.get("/bugs/1")
				.then()
				.statusCode(404);
	}
}