package com.myorg.bugTrackerPoc.controller;

import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.myorg.bugTrackerPoc.entity.Bug;
import com.myorg.bugTrackerPoc.service.BugService;

/**
 * Handles CRUD operations for Bug object :
 *  - Create a new bug
 *  - List all bugs
 *  - Find a bug by id
 */
@RestController
public class BugController {

    private static final Logger LOGGER = Logger.getLogger(BugController.class.getName());

    @Autowired
    private BugService bugService;
    
    @PostMapping("/bugs")
    public ResponseEntity<Bug> addBug(@RequestBody Bug bug){
        Bug newBug = bugService.addBug(bug);
        if(newBug != null){
            return new ResponseEntity<Bug>(newBug, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/bugs")
    public ResponseEntity<Iterable<Bug>> getAllBugs(){
        return new ResponseEntity<>(bugService.getAllBugs(), HttpStatus.OK);
    }

    @GetMapping("/bugs/{id}")
    public ResponseEntity<Bug> findBugById(@PathVariable String id){
        LOGGER.info("Searching for id : " + id);
        Optional<Bug> bug = bugService.findBugById(id);
        if(bug.isPresent()){
            return new ResponseEntity(bug.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
