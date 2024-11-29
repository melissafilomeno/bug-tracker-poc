package com.myorg.bugTrackerPoc.service;

import com.myorg.bugTrackerPoc.entity.Bug;
import com.myorg.bugTrackerPoc.repository.BugRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BugServiceImplTest {

    @InjectMocks
    private BugServiceImpl bugService;

    @Mock
    private BugRepository bugRepository;

    @Test
    public void testCreateBug(){
        Bug mockBug = buildBugEntity("1", "description");
        when(bugRepository.save(any())).thenReturn(mockBug);

        Bug inputBug = new Bug("description");
        Bug result = bugService.addBug(inputBug);
        assertThat(result, hasProperty("id", is("1")));
        assertThat(result, hasProperty("description", is("description")));
    }
    @Test
    public void testGetAllBugs(){
        Bug mockBug1 = buildBugEntity("1", "desc1");
        Bug mockBug2 = buildBugEntity("2", "desc2");
        when(bugRepository.findAll()).thenReturn(List.of(mockBug1, mockBug2));

        Iterable<Bug> result = bugService.getAllBugs();
        assertThat(result, contains(
                hasProperty("id", is("1")),
                hasProperty("id", is("2")))
        );
        assertThat(result, contains(
                hasProperty("description", is("desc1")),
                hasProperty("description", is("desc2"))
        ));
    }

    @Test
    public void testGetBugById(){
        Bug mockBug = buildBugEntity("1", "description");
        when(bugRepository.findById(any())).thenReturn(Optional.of(mockBug));

        Optional<Bug> optionalResult = bugService.findBugById("1");
        assertTrue(optionalResult.isPresent());
        Bug result = optionalResult.get();
        assertThat(result, hasProperty("id", is("1")));
        assertThat(result, hasProperty("description", is("description")));
    }

    private Bug buildBugEntity(String id, String description){
        Bug mockBug = new Bug();
        mockBug.setId(id);
        mockBug.setDescription(description);
        return mockBug;
    }
}
