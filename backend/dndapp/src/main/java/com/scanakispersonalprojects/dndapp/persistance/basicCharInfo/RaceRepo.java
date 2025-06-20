package com.scanakispersonalprojects.dndapp.persistance.basicCharInfo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Race;
import java.util.Optional;


/**
 * Basic JPA repo for access to the race
 * table. Used for it's findById() method
 * to get full information about a race
 */
@Repository
public interface RaceRepo extends JpaRepository<Race, UUID>{

    Optional<Race> findByName(String name);
}
