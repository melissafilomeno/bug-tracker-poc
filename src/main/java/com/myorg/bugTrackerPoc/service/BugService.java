package com.myorg.bugTrackerPoc.service;

import java.util.Optional;

import com.myorg.bugTrackerPoc.entity.Bug;

public interface BugService {
    
    public Iterable<Bug> getAllBugs();

    public Optional<Bug> findBugById(String id);

    public Bug addBug(Bug bug);
}
