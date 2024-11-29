package com.myorg.bugTrackerPoc.mappers;

import com.myorg.bugTrackerPoc.openapi.server.model.Bug;
import org.mapstruct.Mapper;
import java.util.List;
import java.lang.Iterable;

@Mapper(componentModel = "spring")
public interface BugMapper {

    com.myorg.bugTrackerPoc.entity.Bug bugToBugEntity(Bug bug);

    Bug bugEntityToBug(com.myorg.bugTrackerPoc.entity.Bug bugList);
    List<Bug> bugEntityListToBugList(Iterable<com.myorg.bugTrackerPoc.entity.Bug> bugList);
    
}