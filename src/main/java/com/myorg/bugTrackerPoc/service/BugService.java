package com.myorg.bugTrackerPoc.service;

import java.util.Optional;

import com.myorg.bugTrackerPoc.entity.Bug;

public interface BugService {
    
    Iterable<Bug> getAllBugs();

    Optional<Bug> findBugById(String id);

    Bug addBug(Bug bug);
}
