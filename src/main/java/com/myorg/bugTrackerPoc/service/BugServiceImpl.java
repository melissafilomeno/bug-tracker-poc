package com.myorg.bugTrackerPoc.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myorg.bugTrackerPoc.entity.Bug;
import com.myorg.bugTrackerPoc.repository.BugRepository;

@Service
public class BugServiceImpl implements BugService {

    private final BugRepository bugRepository;

    public BugServiceImpl(@Autowired BugRepository bugRepository){
        this.bugRepository = bugRepository;
    }

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
