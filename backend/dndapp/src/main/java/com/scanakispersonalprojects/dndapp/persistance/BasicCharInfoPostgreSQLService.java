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
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.CharacterBasicInfoView;
import com.scanakispersonalprojects.dndapp.model.DndClass;

@Repository
public class BasicCharInfoPostgreSQLService implements BasicCharInfoPersistance{
    
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
  

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public CharacterBasicInfoView getCharInfo(
        UUID uuid){

            
            String race;
            String background;

            try (Connection connection = getConnection()){
                
                ResultSet prs = getResultsSetWithUUID(charInfoTable, uuid, charInfoCol, connection);

                UUID bgUuid = prs.getObject("background_uuid", UUID.class);
                UUID raceUuid = prs.getObject("race_uuid", UUID.class);
            

                race = getNameWithUUID(raceTable, raceUuid, raceCol, connection);
                background = getNameWithUUID(bGTable, bgUuid, bGCol, connection);

                Map<AbilityScore, Integer> map = new TreeMap<>();

                for(AbilityScore as: AbilityScore.values()) {
                    map.put(as, prs.getInt(as.getString()));
                }

                List<DndClass> classes = getClasses(uuid, connection);

                CharacterBasicInfoView result = new CharacterBasicInfoView(uuid, prs.getString("name"), prs.getBoolean("inspiration"), background, bgUuid, race, raceUuid, map, classes);
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
                ps.close();
                return null;
            }
            return rs;
    }

    public String getNameWithUUID(String tableString, UUID uuid, String columnName, Connection connection) throws SQLException{
        final String query = "SELECT * FROM "+ tableString + " WHERE " + columnName + "=?";

        PreparedStatement ps = connection.prepareStatement(query);
     
            ps.setObject(1, uuid);

            ResultSet rs = ps.executeQuery();
            if(!rs.next()) {
                ps.close();
                return null;
            }
            String name = rs.getString("name");
            rs.close();
            return name;
    }

    public List<DndClass> getClasses(UUID charUuid, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(ClassesQuery);
        ps.setObject(1, charUuid);
        ResultSet rs = ps.executeQuery();
        List<DndClass> classes = new ArrayList<>();

        if(rs.next()) {
            UUID classUuid = (UUID)rs.getObject("class_uuid");
            UUID subClassUuid = (UUID)rs.getObject("subclass_uuid");
            short level = (short)rs.getInt("level");

            String className = getNameWithUUID(classTable, classUuid, classCol, connection);
            String subClassName = getNameWithUUID(subClassTable, classUuid, subClassCol, connection);

            classes.add(new DndClass(charUuid, className, subClassUuid, subClassName, level));
        }   
        ps.close();
        return classes;
    }


}

