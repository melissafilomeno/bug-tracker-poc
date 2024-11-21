package com.myorg.bugTrackerPoc.openapi.server.api;

import com.myorg.bugTrackerPoc.openapi.server.api.BugsApiDelegate;
import com.myorg.bugTrackerPoc.openapi.server.model.Bug;
import com.myorg.bugTrackerPoc.service.BugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.myorg.bugTrackerPoc.mappers.BugMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class BugsApiDelegateImpl implements BugsApiDelegate {

    private static final Logger LOGGER = Logger.getLogger(BugsApiDelegateImpl.class.getName());

    @Autowired
    private BugService bugService;

    @Autowired
    private BugMapper bugMapper;

    public ResponseEntity<Bug> bugsPost(Bug bug) {
        com.myorg.bugTrackerPoc.entity.Bug bugEntity = bugMapper.bugToBugEntity(bug);

        com.myorg.bugTrackerPoc.entity.Bug createdBugEntity = bugService.addBug(bugEntity);
        if(createdBugEntity != null){
            Bug bugResponse = bugMapper.bugEntityToBug(createdBugEntity);
            return new ResponseEntity<Bug>(bugResponse, HttpStatus.CREATED);
        }
        throw new ResponseStatusException(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    public ResponseEntity<List<Bug>> findAllBugs() {
        Iterable<com.myorg.bugTrackerPoc.entity.Bug> bugsInDB = bugService.getAllBugs();
        List<Bug> allBugs = new ArrayList<Bug>();
        Bug placeholder = null;
        for(com.myorg.bugTrackerPoc.entity.Bug bug : bugsInDB){
            placeholder = bugMapper.bugEntityToBug(bug);
            allBugs.add(placeholder);
        }
        return new ResponseEntity<List<Bug>>(allBugs, HttpStatus.OK);
    }

    public ResponseEntity<Bug> findBugById(String bugId){
        LOGGER.info("Searching for id : " + bugId);
        Optional<com.myorg.bugTrackerPoc.entity.Bug> bug = bugService.findBugById(bugId);
        if(bug.isPresent()){
            com.myorg.bugTrackerPoc.entity.Bug foundBug = bug.get();
            Bug bugResponse = bugMapper.bugEntityToBug(foundBug);
            return new ResponseEntity<Bug>(bugResponse, HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
