package com.scanakispersonalprojects.dndapp.persistance.basicCharInfo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterInfo;


/**
 * Basic JPA repository to get CharacterInformation
 * Main use is the findById() method
 */

@Repository
public interface CharacterInfoRepo extends JpaRepository<CharacterInfo, UUID>{

}
