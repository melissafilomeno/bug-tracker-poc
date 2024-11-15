package com.myorg.bugTrackerPoc.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.myorg.bugTrackerPoc.entity.Bug;

import java.util.UUID;

@Repository
public interface BugRepository extends CrudRepository<Bug, String>{

}