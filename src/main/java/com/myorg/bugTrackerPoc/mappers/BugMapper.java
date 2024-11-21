package com.myorg.bugTrackerPoc.mappers;

import com.myorg.bugTrackerPoc.openapi.server.model.Bug;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BugMapper {

    com.myorg.bugTrackerPoc.entity.Bug bugToBugEntity(Bug bug);

    Bug bugEntityToBug(com.myorg.bugTrackerPoc.entity.Bug bug);
}