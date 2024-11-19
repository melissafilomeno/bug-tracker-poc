package com.myorg.bugTrackerPoc.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.myorg.bugTrackerPoc.entity.Bug;

@Repository
public interface BugRepository extends CrudRepository<Bug, String>{

}