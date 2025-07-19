package com.scanakispersonalprojects.dndapp.persistance.coinPurse;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.coinPurse.CoinPurse;


/**
 * Basic JPA Repository for accesing the coin_purse postgresql table
 * 
 * Mainly used to search for findById()
 */
@Repository
public interface CoinPurseRepo extends JpaRepository<CoinPurse, UUID> {

    
}
