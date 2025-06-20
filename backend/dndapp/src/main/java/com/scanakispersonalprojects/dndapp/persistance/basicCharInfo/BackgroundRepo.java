package com.scanakispersonalprojects.dndapp.persistance.basicCharInfo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Background;
import java.util.List;

/**
 * Basic JPA Repository for accessing the background postgres table
 * Mainly used to search for findById()
 * 
 */
@Repository
public interface BackgroundRepo extends JpaRepository<Background, UUID>{

    List<Background> findByName(String name);
}
