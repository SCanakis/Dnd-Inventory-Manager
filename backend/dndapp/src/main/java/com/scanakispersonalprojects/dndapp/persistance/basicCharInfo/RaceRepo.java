package com.scanakispersonalprojects.dndapp.persistance.basicCharInfo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Race;
import java.util.Optional;


@Repository
public interface RaceRepo extends JpaRepository<Race, UUID>{

    Optional<Race> findByName(String name);
}
