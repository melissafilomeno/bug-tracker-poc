package com.myorg.bugTrackerPoc.openapi.server.api;

import com.myorg.bugTrackerPoc.mappers.BugMapper;
import com.myorg.bugTrackerPoc.openapi.server.model.Bug;
import com.myorg.bugTrackerPoc.service.BugService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Implementation of BugsApiDelegate interface generated from Open API specifications
 *
 */
@Setter
@Service
public class BugsApiDelegateImpl implements BugsApiDelegate {

    private static final Logger LOGGER = Logger.getLogger(BugsApiDelegateImpl.class.getName());

    private BugService bugService;

    private BugMapper bugMapper;

    public BugsApiDelegateImpl(@Autowired BugService bugService, @Autowired BugMapper bugMapper){
        this.bugService = bugService;
        this.bugMapper = bugMapper;
    }

    public ResponseEntity<Bug> bugsPost(Bug bug) {
        com.myorg.bugTrackerPoc.entity.Bug bugEntity = bugMapper.bugToBugEntity(bug);

        com.myorg.bugTrackerPoc.entity.Bug createdBugEntity = bugService.addBug(bugEntity);
        if(createdBugEntity != null){
            Bug bugResponse = bugMapper.bugEntityToBug(createdBugEntity);
            return new ResponseEntity<>(bugResponse, HttpStatus.CREATED);
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<List<Bug>> findAllBugs() {
        Iterable<com.myorg.bugTrackerPoc.entity.Bug> bugsInDB = bugService.getAllBugs();
        List<Bug> allBugs = bugMapper.bugEntityListToBugList(bugsInDB);
        return new ResponseEntity<>(allBugs, HttpStatus.OK);
    }

    public ResponseEntity<Bug> findBugById(String bugId){
        LOGGER.info("Searching for id : " + bugId);
        Optional<com.myorg.bugTrackerPoc.entity.Bug> bug = bugService.findBugById(bugId);
        if(bug.isPresent()){
            com.myorg.bugTrackerPoc.entity.Bug foundBug = bug.get();
            Bug bugResponse = bugMapper.bugEntityToBug(foundBug);
            return new ResponseEntity<>(bugResponse, HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
