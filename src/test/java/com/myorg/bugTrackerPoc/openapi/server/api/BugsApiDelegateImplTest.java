package com.myorg.bugTrackerPoc.openapi.server.api;

import com.myorg.bugTrackerPoc.entity.Bug;
import com.myorg.bugTrackerPoc.exception.ConnectionExceptionHandler;
import com.myorg.bugTrackerPoc.exception.RestExceptionHandler;
import com.myorg.bugTrackerPoc.mappers.BugMapper;
import com.myorg.bugTrackerPoc.service.BugService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BugsApiDelegateImplTest {

    @InjectMocks
    private BugsApiDelegateImpl apiDelegate;

    @Mock
    private BugService bugService;

    @Mock
    private BugMapper bugMapper;

    @BeforeEach
    public void setUp(){
        standaloneSetup(new BugsApiController(apiDelegate), new RestExceptionHandler(), new ConnectionExceptionHandler());
    }

    @Test
    public void testCreateBug_Success(){
        when(bugService.addBug(any())).thenReturn(mock(Bug.class));

        String id = "1";
        String description = "This is my first bug";
        when(bugMapper.bugEntityToBug(any())).thenReturn(buildBugObject(id, description));

        given()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body("{\"description\": \"" + description + "\"}")
            .post("/bugs")
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("id", is(id))
            .body("description", is(description));
    }

    @Test
    public void testGetBugById_Success(){
        when(bugService.findBugById(any())).thenReturn(Optional.of(mock(Bug.class)));
        String id = "1";
        String description = "This is my first bug";
        com.myorg.bugTrackerPoc.openapi.server.model.@NotNull Bug mockBug = buildBugObject(id, description);
        when(bugMapper.bugEntityToBug(any())).thenReturn(mockBug);

        given()
            .get("/bugs/" + id)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", is(id))
            .body("description", is(description));
    }

    @Test
    public void testGetBugById_Fail_NotFound(){
        when(bugService.findBugById(any())).thenReturn(Optional.empty());

        given()
            .get("/bugs/1")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("code", is(String.valueOf(HttpStatus.NOT_FOUND.value())))
            .body("message", is("Not Found"));
    }

    @Test
    public void testGetAllBugs_Success(){
        Bug mockBugEntity1 = buildBugEntityObject("1", "desc1");
        Bug mockBugEntity2 = buildBugEntityObject("2", "desc2");
        when(bugService.getAllBugs()).thenReturn(List.of(mockBugEntity1, mockBugEntity2));
        com.myorg.bugTrackerPoc.openapi.server.model.Bug mockBug1 = buildBugObject("1", "desc1");
        com.myorg.bugTrackerPoc.openapi.server.model.Bug mockBug2 = buildBugObject("2", "desc2");
        when(bugMapper.bugEntityListToBugList(any())).thenReturn(List.of(mockBug1, mockBug2));

        given()
        .when()
            .get("/bugs")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(2));
    }

    private Bug buildBugEntityObject(String id, String description){
        Bug mockBugEntity = new Bug();
        mockBugEntity.setId(id);
        mockBugEntity.setDescription(description);
        return mockBugEntity;
    }

    private com.myorg.bugTrackerPoc.openapi.server.model.Bug buildBugObject(String id, String description) {
        com.myorg.bugTrackerPoc.openapi.server.model.Bug mockBug = new com.myorg.bugTrackerPoc.openapi.server.model.Bug();
        mockBug.setId(id);
        mockBug.setDescription(description);
        return mockBug;
    }

}
