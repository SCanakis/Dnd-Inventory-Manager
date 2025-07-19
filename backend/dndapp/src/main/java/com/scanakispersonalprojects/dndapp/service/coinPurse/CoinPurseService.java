package com.scanakispersonalprojects.dndapp.service.coinPurse;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scanakispersonalprojects.dndapp.model.coinPurse.CoinPurse;
import com.scanakispersonalprojects.dndapp.model.coinPurse.CoinPurseDTO;
import com.scanakispersonalprojects.dndapp.persistance.coinPurse.CoinPurseRepo;


/**
 * Service class for managing coin_purse information.
 * 
 * Hanldes coin_purse information retrival, updating, and creation.
 * 
 */
@Service
public class CoinPurseService {

    // coin-pruse repo
    private CoinPurseRepo repo;


    /**
     * Contructor for dpenednecy injuection of the coin purse repo.
     * @param repo
     */
    public CoinPurseService(CoinPurseRepo repo) {
        this.repo = repo;
    }


    /**
     * Retrives a character's coin purse by their UUID (read-only)
     * 
     * @param charUuid 
     * @return the character's coin purse, or null if not found or charUuid is not null
     */
    @Transactional(readOnly = true) 
    public CoinPurse getCoinPurse(UUID charUuid) {
        if(charUuid != null) {
            return repo.findById(charUuid).orElse(null);
        } else {
            return null;
        }
    }
    
    /**
     * Updates a cahracter's coin purse with the provided partial udpates. 
     * 
     * This method perfroms a transactional update operation, applying only 
     * the coin types that have vlaues other than -1 in the DTO.
     * 
     * 
     * @param charUuid
     * @param update - the DTO cointaing the coin mamounts to updates.
     * 
     * @return -  true if updates was succesful, false if charUUid, update is null,
     *          or character does not exits
     */
    @Transactional 
    public boolean updateCoinPurse(UUID charUuid, CoinPurseDTO update) {
        if(charUuid == null || update == null) {
            return false;
        }

        CoinPurse coinPurse = getCoinPurse(charUuid);

        if(update.getPlatinum() > -1) {
            coinPurse.setPlatinum(update.getPlatinum());
        }
        if(update.getGold() > -1) {
            coinPurse.setGold(update.getGold());
        }
        if(update.getElectrum() > -1) {
            coinPurse.setElectrum(update.getElectrum());
        }
        if(update.getSilver() > -1) {
            coinPurse.setSilver(update.getSilver());
        }
        if(update.getCopper() > -1) {
            coinPurse.setCopper(update.getCopper());
        }

        repo.save(coinPurse);

        return true;
    }

    /**
     * Creats a new coin purse for a characte rwith all coin amounts set to zero. 
     * 
     * @param charUuid
     */
    public void createFreshCoinPurse(UUID charUuid) {
        CoinPurse coinPurse = new CoinPurse(charUuid, 0, 0, 0, 0, 0);
        repo.save(coinPurse);
    }

}


