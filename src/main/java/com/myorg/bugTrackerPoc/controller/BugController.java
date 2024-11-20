package com.myorg.bugTrackerPoc.controller;

import com.myorg.bugTrackerPoc.service.BugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.openapitools.client.model.Bug;

/**
 * Handles CRUD operations for Bug object :
 *  - Create a new bug
 *  - List all bugs
 *  - Find a bug by id
 */
@RestController
@RequestMapping(value = "/bugs")
public class BugController {

    private static final Logger LOGGER = Logger.getLogger(BugController.class.getName());

    @Autowired
    private BugService bugService;
    
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Bug addBug(@RequestBody Bug bugRequest){
        com.myorg.bugTrackerPoc.entity.Bug bugEntity = new com.myorg.bugTrackerPoc.entity.Bug();
        bugEntity.setDescription(bugRequest.getDescription());

        com.myorg.bugTrackerPoc.entity.Bug createdBugEntity = bugService.addBug(bugEntity);
        if(createdBugEntity != null){
            Bug bugResponse = new Bug();
            bugResponse.setId(createdBugEntity.getId());
            bugResponse.setDescription(createdBugEntity.getDescription());
            return bugResponse;
        }
        throw new ResponseStatusException(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Bug> getAllBugs(){
        Iterable<com.myorg.bugTrackerPoc.entity.Bug> bugsInDB = bugService.getAllBugs();
        List<Bug> allBugs = new ArrayList<Bug>();
        Bug placeholder = null;
        for(com.myorg.bugTrackerPoc.entity.Bug bug : bugsInDB){
            placeholder = new Bug();
            placeholder.setId(bug.getId());
            placeholder.setDescription(bug.getDescription());
            allBugs.add(placeholder);
        }
        return allBugs;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Bug findBugById(@PathVariable String id){
        LOGGER.info("Searching for id : " + id);
        Optional<com.myorg.bugTrackerPoc.entity.Bug> bug = bugService.findBugById(id);
        if(bug.isPresent()){
            com.myorg.bugTrackerPoc.entity.Bug foundBug = bug.get();
            Bug bugResponse = new Bug();
            bugResponse.setId(foundBug.getId());
            bugResponse.setDescription(foundBug.getDescription());
            return bugResponse;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

}
