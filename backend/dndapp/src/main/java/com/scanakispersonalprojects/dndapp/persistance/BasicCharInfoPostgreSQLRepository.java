package com.scanakispersonalprojects.dndapp.persistance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.scanakispersonalprojects.dndapp.model.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.CharViewPatch;
import com.scanakispersonalprojects.dndapp.model.CharacterBasicInfoView;
import com.scanakispersonalprojects.dndapp.model.DeathSavingThrowsHelper;
import com.scanakispersonalprojects.dndapp.model.DndClass;
import com.scanakispersonalprojects.dndapp.model.HPHandler;

@Transactional
@Repository
public class BasicCharInfoPostgreSQLRepository implements BasicCharInfoPersistance{
    
    @Autowired
    private DataSource dataSource;
    final String charInfoTable= "characters_info";
    final String charInfoCol= "char_info_uuid";

    final String raceTable= "race";
    final String raceCol= "race_uuid";

    final String bGTable= "background";
    final String bGCol= "background_uuid";

    final String ClassesQuery = "SELECT * FROM character_class WHERE char_info_uuid=?";

    final String classTable="class";
    final String classCol="class_uuid";

    final String subClassTable="subclass";
    final String subClassCol="subclass_uuid";

    final String hpHandlerTable="hp_handler";
    final String hpHandlerCol="char_info_uuid";

    final String deathSavingThrowTable="death_saving_throws";
    final String deathSavingThrowCol="char_info_uuid";
    

    // inspiration query
    // hitDice query
    // AbilityScore query
    // DeathSaving throw

    

    private Connection getConnection() throws SQLException {
        return DataSourceUtils.getConnection(dataSource);
    }

    @Override
    public CharacterBasicInfoView getCharInfo(
        UUID uuid){

            
            String race;
            String background;
            Connection connection= null;
            try {
                
                connection = getConnection();
                ResultSet prs = getResultsSetWithUUID(charInfoTable, uuid, charInfoCol, connection);

                UUID bgUuid = prs.getObject("background_uuid", UUID.class);
                UUID raceUuid = prs.getObject("race_uuid", UUID.class);
            

                race = getNameWithUUID(raceTable, raceUuid, raceCol, connection);
                background = getNameWithUUID(bGTable, bgUuid, bGCol, connection);

                Map<AbilityScore, Integer> map = new TreeMap<>();

                for(AbilityScore as: AbilityScore.values()) {
                    map.put(as, prs.getInt(as.getString()));
                }

            
                CharacterBasicInfoView result = new CharacterBasicInfoView(
                    uuid,
                    prs.getString("name"),
                    prs.getBoolean("inspiration"),
                    background,
                    bgUuid,
                    race,
                    raceUuid,
                    map,
                    getClasses(uuid, connection), 
                    getHpHandler(uuid, connection),
                    getDeathSavingThrowsHelper(uuid, connection));

                return result;

         
            } catch (Exception e) {
                return null;
            } finally {
                DataSourceUtils.releaseConnection(connection, dataSource);
            }
        }

    private ResultSet getResultsSetWithUUID(String tableString, UUID uuid, String columnName, Connection connection) throws SQLException{
        final String query = "SELECT * FROM "+ tableString + " WHERE " + columnName + "=?";

        PreparedStatement ps = connection.prepareStatement(query);
     
            ps.setObject(1, uuid);

            ResultSet rs = ps.executeQuery();
            if(!rs.next()) {
                return null;
            }
            return rs;
    }

    private String getNameWithUUID(String tableString, UUID uuid, String columnName, Connection connection) throws SQLException{
        final String query = "SELECT * FROM "+ tableString + " WHERE " + columnName + "=?";

        PreparedStatement ps = connection.prepareStatement(query);
     
            ps.setObject(1, uuid);

            ResultSet rs = ps.executeQuery();
            if(!rs.next()) {
                return null;
            }
            String name = rs.getString("name");
            return name;
    }

    private List<DndClass> getClasses(UUID charUuid, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(ClassesQuery);
        ps.setObject(1, charUuid);
        ResultSet rs = ps.executeQuery();
        List<DndClass> classes = new ArrayList<>();

        while(rs.next()) {
            UUID classUuid = (UUID)rs.getObject("class_uuid");
            UUID subClassUuid = (UUID)rs.getObject("subclass_uuid");
            short level = (short)rs.getInt("level");
            short hitDice = (short)rs.getInt("hit_dice_remaining");
            ResultSet crs = getResultsSetWithUUID(classTable, classUuid, classCol, connection);
            String className = crs.getString("name");
            short hitDiceValue = (short)crs.getInt("hit_dice_value");

            String subClassName = getNameWithUUID(subClassTable, subClassUuid, subClassCol, connection);

            classes.add(new DndClass(charUuid, className, subClassUuid, subClassName, level, hitDice, hitDiceValue));
        }   
        return classes;
    }

    private HPHandler getHpHandler(UUID charUuid, Connection connection) throws SQLException  {
        ResultSet rs = getResultsSetWithUUID(hpHandlerTable, charUuid, hpHandlerCol, connection);
        
        HPHandler hpHandler = new HPHandler(rs.getInt("current_hp"), rs.getInt("max_hp"), rs.getInt("temp_hp"));
        return hpHandler;
    }

    private DeathSavingThrowsHelper getDeathSavingThrowsHelper(UUID charUuid, Connection connection) throws SQLException  {
        ResultSet rs = getResultsSetWithUUID(deathSavingThrowTable, charUuid, deathSavingThrowCol, connection);
        
        DeathSavingThrowsHelper deathSavingThrowsHelper = new DeathSavingThrowsHelper((short) rs.getInt("success"), (short)rs.getInt("failure"));
        return deathSavingThrowsHelper;
    }


    final String updateCurrentHealthQuery="UPDATE hp_handler SET current_hp=? WHERE char_info_uuid=?";
    final String updateTempHealthQuery="UPDATE hp_handler SET temp_hp=? WHERE char_info_uuid=?";
    final String updateNameQuery="UPDATE characters_info SET name=? WHERE char_info_uuid=?";

    final String updateHitDiceQuery="UPDATE character_class SET hit_dice_remaining=? WHERE class_uuid=? AND char_info_uuid=?";

    final String updateInspirationQuery="UPDATE characters_info SET inspiration=? WHERE char_info_uuid=?";
    final String updateAbilityScore1Query= "UPDATE characters_info SET ";
    final String updateAbilityScore2Query= "=? WHERE char_info_uuid=?";
    final String updateSuccessThrow = "UPDATE death_saving_throws SET success=? WHERE char_info_uuid=?";
    final String updateFailedThrow = "UPDATE death_saving_throws SET failure=? WHERE char_info_uuid=?";
    
    @Override
    public CharacterBasicInfoView updateCharInfo(UUID uuid, CharViewPatch updatedView) {
        try {
            Connection connection = getConnection();
            if(updatedView.name()!= null  && updatedView.name().length() < 50) {
                PreparedStatement ps =connection.prepareStatement(updateNameQuery);
                ps.setString(1, updatedView.name());  
                ps.setObject(2, uuid); 
                ps.executeUpdate();
            }
            if(updatedView.currentHP() != -1 && updatedView.currentHP() >= 0) {
                PreparedStatement ps = connection.prepareStatement(updateCurrentHealthQuery);
                ps.setInt(1, updatedView.currentHP());
                ps.setObject(2, uuid); 
                ps.executeUpdate();
            }
            if(updatedView.tempHP() != -1 && updatedView.tempHP() >= 0){
                PreparedStatement ps = connection.prepareStatement(updateTempHealthQuery);
                ps.setInt(1, updatedView.tempHP());
                ps.setObject(2, uuid); 
                ps.executeUpdate();
            }
            for(Map.Entry<UUID,Integer> entry: updatedView.hitDice().entrySet()) {
                UUID classUuid = entry.getKey();
                int hitDice = (int)entry.getValue();
                PreparedStatement ps = connection.prepareStatement(updateHitDiceQuery);
                ps.setInt(1, hitDice);
                ps.setObject(2, classUuid);
                ps.setObject(3, uuid);
                ps.executeUpdate();
            }
            if(updatedView.inspiration() != null) {
                PreparedStatement ps = connection.prepareStatement(updateInspirationQuery);
                ps.setBoolean(1, updatedView.inspiration());
                ps.setObject(2, uuid); 
                ps.executeUpdate();
            }
            for(Map.Entry<AbilityScore,Integer> entry: updatedView.abilityScore().entrySet()) {
                AbilityScore ability = entry.getKey();
                int abilityValue = (int)entry.getValue();
                PreparedStatement ps = connection.prepareStatement(updateAbilityScore1Query + ability.getString() + updateAbilityScore2Query);
                ps.setInt(1, abilityValue);
                ps.setObject(2, uuid);
                ps.executeUpdate();
            }
            if(updatedView.success() != -1) {
                PreparedStatement ps = connection.prepareStatement(updateSuccessThrow);
                ps.setInt(1, updatedView.success());
                ps.setObject(2, uuid); 
                ps.executeUpdate();
            }
            if(updatedView.failure() != -1) {
                PreparedStatement ps = connection.prepareStatement(updateFailedThrow);
                ps.setInt(1, updatedView.failure());
                ps.setObject(2, uuid); 
                ps.executeUpdate();
            }
            return getCharInfo(uuid);
        
        }  catch (Exception e) {
            return null;
        }
    }
        


}

