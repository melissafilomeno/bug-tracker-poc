package com.cgi.bug_tracker_poc.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cgi.bug_tracker_poc.model.Bug;
import java.util.Calendar;

@RestController
public class BugController {
    
    @GetMapping("/bugs")
    public List<Bug> getAllBugs(){
        List<Bug> bugs = new ArrayList<>();
        Bug bug = new Bug("description", Calendar.getInstance());
        bugs.add(bug);

        return bugs;
    }

    @GetMapping("/bugs/{id}")
    public Bug getBugById(@PathVariable String id){
        Bug bug = new Bug("description", Calendar.getInstance());
        return bug;
    }

}
