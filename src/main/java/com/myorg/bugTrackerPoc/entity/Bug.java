package com.myorg.bugTrackerPoc.entity;

import lombok.Data;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;

import java.util.UUID;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;


@Data
@Table(name = "BUG")
@Entity
public class Bug {
    
    @Column(name = "BUG_UUID")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "BUG_DESCRIPTION")
    private String description;

    public Bug(){

    }

    public Bug(String description){
        this.description = description;
    }
    
}
