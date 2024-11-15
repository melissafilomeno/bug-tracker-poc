package com.myorg.bugTrackerPoc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myorg.bugTrackerPoc.entity.Bug;
import com.myorg.bugTrackerPoc.jpa.BugRepository;

@Service
public class BugServiceImpl implements BugService {
    
    @Autowired
    private BugRepository bugRepository;

    @Override
    public Iterable<Bug> getAllBugs() {
        return bugRepository.findAll();
    }

    @Override
    public Optional<Bug> findBugById(String id) {
        return bugRepository.findById(id);
    }

    @Override
    public Bug addBug(Bug bug) {
        return bugRepository.save(bug);
    }
    
}
