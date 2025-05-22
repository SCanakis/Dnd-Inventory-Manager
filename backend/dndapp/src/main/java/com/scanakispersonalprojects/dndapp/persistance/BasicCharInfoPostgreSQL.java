package com.scanakispersonalprojects.dndapp.persistance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.scanakispersonalprojects.dndapp.model.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.CharacterBasicInfoView;

@Component
public class BasicCharInfoPostgreSQL implements BasicCharInfoPersistance{
    
    @Autowired
    private DataSource dataSource;
  

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public CharacterBasicInfoView getCharInfo(
        String uuid){

            final String SQL_CHAR= "SELECT * FROM characters_info WHERE char_info_uuid=?";
            final String SQL_RACE= "SELECT name FROM race WHERE race_uuid=?";
            final String SQL_BG= "SELECT * FROM background where background_uuid=?";
            String race;
            String background;
            try {
               
            Connection connection = getConnection();

            PreparedStatement psChar = connection.prepareStatement(SQL_CHAR);
            psChar.setObject(1, UUID.fromString(uuid));
            ResultSet rs = psChar.executeQuery();
            if(!rs.next()) {
                System.out.println(rs.next());
                return null;
            }
            UUID bgUuid = rs.getObject("background_uuid", UUID.class);
            UUID raceUuid = rs.getObject("race_uuid", UUID.class);
        
            PreparedStatement psRace = connection.prepareStatement(SQL_RACE);
            psRace.setObject(1, raceUuid);

            ResultSet rrs = psRace.executeQuery();
            if(!rrs.next()){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "RACE NOT FOUND " + raceUuid);
            }
            
            race = rrs.getString("name");
                
            
            PreparedStatement psBG = connection.prepareStatement(SQL_BG);
            psBG.setObject(1, bgUuid);

            ResultSet brs = psBG.executeQuery();
            if(!brs.next()){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "BACKGROUND NOT FOUND " + bgUuid);
            }
            background= brs.getString("name");        

            Map<AbilityScore, Integer> map = new TreeMap<>();

            for(AbilityScore as: AbilityScore.values()) {
                map.put(as, rs.getInt(as.getString()));
            }

            return new CharacterBasicInfoView(uuid, rs.getString("name"), rs.getBoolean("inspiration"), background, bgUuid, race, raceUuid, map);

         
            } catch (Exception e) {
                return null;
            }
        }

}

