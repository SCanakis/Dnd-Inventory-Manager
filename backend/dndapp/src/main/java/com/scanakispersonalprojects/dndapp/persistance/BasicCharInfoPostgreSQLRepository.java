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

import com.scanakispersonalprojects.dndapp.model.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.CharacterBasicInfoView;
import com.scanakispersonalprojects.dndapp.model.DeathSavingThrowsHelper;
import com.scanakispersonalprojects.dndapp.model.DndClass;
import com.scanakispersonalprojects.dndapp.model.HPHandler;
import com.scanakispersonalprojects.dndapp.model.HealthUpdate;

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
    
    final String updateHealthQuery="UPDATE hp_handler SET current_hp=?, temp_hp=? WHERE char_info_uuid=?";
    final String updateNameQuery="UPDATE characters_info SET name=? WHERE char_info_uuid=?";
  

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

    @Override
    public CharacterBasicInfoView updateHealth(UUID uuid, HealthUpdate newHealth){
        try {
           Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(updateHealthQuery);
            ps.setObject(1, newHealth.newHealth());
            ps.setObject(2, newHealth.tempHealth());
            ps.setObject(3, uuid);
            ps.executeUpdate();

            return getCharInfo(uuid); 
        } catch (Exception e) {
            return null;
        }   
    }

    @Override
    public CharacterBasicInfoView updateName(UUID uuid, String name) {
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(updateNameQuery);
            ps.setString(1, name);
            ps.setObject(2, uuid);

            ps.executeUpdate();
            return getCharInfo(uuid);
        } catch (Exception e) {
            return null;
        }

    }


}

