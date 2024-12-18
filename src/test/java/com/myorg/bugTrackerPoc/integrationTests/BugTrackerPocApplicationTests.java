package com.myorg.bugTrackerPoc.integrationTests;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.myorg.bugTrackerPoc.entity.Bug;
import com.myorg.bugTrackerPoc.repository.BugRepository;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.restassured.RestAssured;
import io.restassured.authentication.OAuthSignature;
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
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BugTrackerPocApplicationTest {

	@LocalServerPort
	private Integer port;

	private static RSAKey rsaKey;
	private static final String KEY_ID = "123456";

	@Container
	@ServiceConnection
	private final static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:latest"))
			.withInitScript("db/db_schema.sql");

	@Container
	private final static WireMockContainer wireMockContainer = new WireMockContainer("wiremock/wiremock:2.35.0");

	@BeforeAll
	public static void beforeAll() throws Exception{
		mySQLContainer.start();
		wireMockContainer.start();

		rsaKey = new RSAKeyGenerator(2048)
				.keyUse(KeyUse.SIGNATURE)
				.algorithm(new Algorithm("RS256"))
				.keyID(KEY_ID)
				.generate();

		RSAKey rsaPublicJWK = rsaKey.toPublicJWK();
		String jwkResponse = String.format("{\"keys\": [%s]}", rsaPublicJWK.toJSONString());

		WireMock wireMockAdminClient = new WireMock(wireMockContainer.getHost(), wireMockContainer.getPort());
		wireMockAdminClient.register(
			get(urlPathEqualTo("/"))
				.willReturn(aResponse()
					.withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
					.withBody(jwkResponse))
		);
	}

	private String getSignedJwt() throws Exception {
		RSASSASigner signer = new RSASSASigner(rsaKey);
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
				.claim("scope", "reader")
				.expirationTime(new Date(new Date().getTime() + 60 * 1000))
				.build();
		SignedJWT signedJwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256)
				.keyID(rsaKey.getKeyID()).build(), claimsSet);
		signedJwt.sign(signer);
		return signedJwt.serialize();
	}

	@AfterAll
	public static void afterAll(){
		mySQLContainer.stop();
		wireMockContainer.stop();
	}

	@DynamicPropertySource
	public static void configureProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
		dynamicPropertyRegistry.add("spring.datasource.username", mySQLContainer::getUsername);
		dynamicPropertyRegistry.add("spring.datasource.password", mySQLContainer::getPassword);
		dynamicPropertyRegistry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", wireMockContainer::getBaseUrl);
	}

	@Autowired
	private BugRepository bugRepository;

	@BeforeEach
	public void setUp(){
		RestAssured.baseURI = "http://localhost:" + port;
		bugRepository.deleteAll();
	}

	@Test
	public void saveBug_Success() throws Exception {
		String description = "My first bug";
		given()
			.auth().oauth2(getSignedJwt(), OAuthSignature.HEADER)
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
	public void saveBug_Fail_DescriptionExceedsMaxLength() throws Exception {
		String description = "01234567890123456789012345678901234567890";
		given()
			.auth().oauth2(getSignedJwt(), OAuthSignature.HEADER)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.body("{\"description\": \"" + description + "\"}")
			.post("/bugs")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("code", is(String.valueOf(HttpStatus.BAD_REQUEST.value())))
			.body("message", is("Bad Request"));
	}

	@Test
	public void saveBug_Fail_DescriptionIsNull() throws Exception {
		given()
			.auth().oauth2(getSignedJwt(), OAuthSignature.HEADER)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.body("{\"description\": null }")
			.post("/bugs")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("code", is(String.valueOf(HttpStatus.BAD_REQUEST.value())))
			.body("message", is("Bad Request"));
	}

	@Test
	public void saveBug_Fail_DescriptionIsEmptyString() throws Exception {
		String description = "";
		given()
			.auth().oauth2(getSignedJwt(), OAuthSignature.HEADER)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.body("{\"description\": \"" + description + "\"}")
			.post("/bugs")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("code", is(String.valueOf(HttpStatus.BAD_REQUEST.value())))
			.body("message", is("Bad Request"));
	}

	@Test
	public void saveBug_Fail_DescriptionIsOnlySpaces() throws Exception {
		String description = " ";
		given()
			.auth().oauth2(getSignedJwt(), OAuthSignature.HEADER)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.body("{\"description\": \"" + description + "\"}")
			.post("/bugs")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("code", is(String.valueOf(HttpStatus.BAD_REQUEST.value())))
			.body("message", is("Bad Request"));
	}

	@Test
	public void getBugById_Success() throws Exception {
		String description = "My first bug";
		Bug bug = new Bug(description);
		Bug insertedBug = bugRepository.save(bug);

		given()
			.auth().oauth2(getSignedJwt(), OAuthSignature.HEADER)
			.get("/bugs/" + insertedBug.getId())
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("description", is(description))
			.body("id", is(insertedBug.getId()));
	}

	@Test
	public void getBugById_Fail_NotFound() throws Exception {
		String id = "f100bc19-f816-4b57-bea1-011119091dae";
		given()
			.auth().oauth2(getSignedJwt(), OAuthSignature.HEADER)
			.get("/bugs/" + id)
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("code", is(String.valueOf(HttpStatus.NOT_FOUND.value())))
			.body("message", is("Not Found"));
	}

	@Test
	public void getBugById_Fail_InvalidFormat() throws Exception {
		given()
			.auth().oauth2(getSignedJwt(), OAuthSignature.HEADER)
			.get("/bugs/1")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("code", is(String.valueOf(HttpStatus.BAD_REQUEST.value())))
			.body("message", is("Bad Request"));
	}

	@Test
	public void getAllBugs_Success() throws Exception {
		List<Bug> bugList = new ArrayList<>();
		bugList.add(new Bug("desc1"));
		bugList.add(new Bug("desc2"));
		bugRepository.saveAll(bugList);

		given()
			.auth().oauth2(getSignedJwt(), OAuthSignature.HEADER)
			.get("/bugs")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body(".", hasSize(2));
	}

}