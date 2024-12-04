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
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
class BugTrackerPocApplicationTest {

	@LocalServerPort
	private Integer port;

	@Container
	@ServiceConnection
	private final static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:latest"))
			.withInitScript("db/db_schema.sql");

	@BeforeAll
	public static void beforeAll(){
		mySQLContainer.start();
	}

	@AfterAll
	public static void afterAll(){
		mySQLContainer.stop();
	}

	@DynamicPropertySource
	public static void configureProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
		dynamicPropertyRegistry.add("spring.datasource.username", mySQLContainer::getUsername);
		dynamicPropertyRegistry.add("spring.datasource.password", mySQLContainer::getPassword);
	}

	@Autowired
	private BugRepository bugRepository;

	@BeforeEach
	public void setUp(){
		RestAssured.baseURI = "http://localhost:" + port;
		bugRepository.deleteAll();
	}

	@Test
	public void saveBug_Success() {
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
	public void saveBug_Fail_DescriptionExceedsMaxLength(){
		String description = "01234567890123456789012345678901234567890";
		given()
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.body("{\"description\": \"" + description + "\"}")
			.post("/bugs")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("code", is(String.valueOf(HttpStatus.BAD_REQUEST.value())))
			.body("message", is("Bad Request"));
	}

	@Test
	public void saveBug_Fail_DescriptionIsNull(){
		given()
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.body("{\"description\": null }")
			.post("/bugs")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("code", is(String.valueOf(HttpStatus.BAD_REQUEST.value())))
			.body("message", is("Bad Request"));
	}

	@Test
	public void saveBug_Fail_DescriptionIsEmptyString(){
		String description = "";
		given()
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.body("{\"description\": \"" + description + "\"}")
			.post("/bugs")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("code", is(String.valueOf(HttpStatus.BAD_REQUEST.value())))
			.body("message", is("Bad Request"));
	}

	@Test
	public void saveBug_Fail_DescriptionIsOnlySpaces(){
		String description = " ";
		given()
		.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
		.body("{\"description\": \"" + description + "\"}")
		.post("/bugs")
	.then()
		.statusCode(HttpStatus.BAD_REQUEST.value())
		.body("code", is(String.valueOf(HttpStatus.BAD_REQUEST.value())))
		.body("message", is("Bad Request"));
	}

	@Test
	public void getBugById_Success(){
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
	public void getBugById_Fail_NotFound(){
		String id = "f100bc19-f816-4b57-bea1-011119091dae";
		given()
			.get("/bugs/" + id)
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("code", is(String.valueOf(HttpStatus.NOT_FOUND.value())))
			.body("message", is("Not Found"));
	}

	@Test
	public void getBugById_Fail_InvalidFormat(){
		given()
			.get("/bugs/1")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("code", is(String.valueOf(HttpStatus.BAD_REQUEST.value())))
			.body("message", is("Bad Request"));
	}

	@Test
	public void getAllBugs_Success(){
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

}