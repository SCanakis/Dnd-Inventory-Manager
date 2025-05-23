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
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.CharacterBasicInfoView;

@Repository
public class BasicCharInfoPostgreSQLService implements BasicCharInfoPersistance{
    
    @Autowired
    private DataSource dataSource;
  

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public CharacterBasicInfoView getCharInfo(
        UUID uuid){

            final String CharInfoTable= "characters_info";
            final String CharInfoCol= "char_info_uuid";

            final String RaceTable= "race";
            final String RaceCol= "race_uuid";

            final String BGTable= "background";
            final String BGCol= "background_uuid";

            String race;
            String background;

            try (Connection connection = getConnection()){
                
                ResultSet prs = getResultsSetWithUUID(CharInfoTable, uuid, CharInfoCol, connection);

                UUID bgUuid = prs.getObject("background_uuid", UUID.class);
                UUID raceUuid = prs.getObject("race_uuid", UUID.class);
            

                ResultSet rrs = getResultsSetWithUUID(RaceTable, raceUuid, RaceCol, connection);
                race = rrs.getString("name");
                rrs.close();

                ResultSet brs = getResultsSetWithUUID(BGTable, bgUuid, BGCol, connection);
                background= brs.getString("name");        
                brs.close();

                Map<AbilityScore, Integer> map = new TreeMap<>();

                for(AbilityScore as: AbilityScore.values()) {
                    map.put(as, prs.getInt(as.getString()));
                }
                CharacterBasicInfoView result = new CharacterBasicInfoView(uuid, prs.getString("name"), prs.getBoolean("inspiration"), background, bgUuid, race, raceUuid, map);
                prs.close();

                return result;

         
            } catch (Exception e) {
                return null;
            }
        }

    public ResultSet getResultsSetWithUUID(String tableString, UUID uuid, String columnName, Connection connection) throws SQLException{
        final String query = "SELECT * FROM "+ tableString + " WHERE " + columnName + "=?";

        PreparedStatement ps = connection.prepareStatement(query);
     
            ps.setObject(1, uuid);

            ResultSet rs = ps.executeQuery();
            if(!rs.next()) {
                System.out.println(rs.next());
                ps.close();
                return null;
            }
            return rs;
    

    }

}

