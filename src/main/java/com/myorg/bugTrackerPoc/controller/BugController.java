package com.myorg.bugTrackerPoc.controller;

import com.myorg.bugTrackerPoc.entity.Bug;
import com.myorg.bugTrackerPoc.service.BugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.logging.Logger;

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

    @Autowired
    private MessageSource messageSource;
    
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Bug addBug(@RequestBody Bug bug){
        Bug newBug = bugService.addBug(bug);
        if(newBug != null){
            return newBug;
        }
        throw new ResponseStatusException(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @GetMapping
    public ResponseEntity<Iterable<Bug>> getAllBugs(){
        return new ResponseEntity<>(bugService.getAllBugs(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bug> findBugById(@PathVariable String id){
        LOGGER.info("Searching for id : " + id);
        Optional<Bug> bug = bugService.findBugById(id);
        if(bug.isPresent()){
            return new ResponseEntity(bug.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
