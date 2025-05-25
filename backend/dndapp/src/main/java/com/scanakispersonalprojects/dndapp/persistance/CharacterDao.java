package com.scanakispersonalprojects.dndapp.persistance;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.config.SqlFileLoader;
import com.scanakispersonalprojects.dndapp.model.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.CharacterBasicInfoView;
import com.scanakispersonalprojects.dndapp.model.DeathSavingThrowsHelper;
import com.scanakispersonalprojects.dndapp.model.DndClass;
import com.scanakispersonalprojects.dndapp.model.HPHandler;



/**
 * 
 * Data access boejct for CRUD operation on D&D character basic information.
 * 
 * 
 * Uses {@link NamedParameterJdbcTemplate} to execute SQL statements
 * loaded via {@link SqlFileLoader}. Provides methods to update individual
 * character attributes and to retrieve a full {@link CharacterBasicInfoView}
 * 
 */

@Repository
public class CharacterDao {
    
    private final NamedParameterJdbcTemplate jdbc;
    private final SqlFileLoader sql;
    

    /**
     * Constrcuts a new {@link CharacterDao} with the given JDBC template and SQL loader.
     * 
     * @param jdbc  the {@link NamedParameterJdbcTemplate} for executing parameterized queries
     * @param sql   the {@link SqlFileLoader} for loading SQL scripts by name.
     */

    public CharacterDao(NamedParameterJdbcTemplate jdbc, SqlFileLoader sql) {
        this.jdbc = jdbc;
        this.sql = sql;
    }


    /**
     * Updates the current hit points for a character
     * 
     * @param charUuid      the UUID of the character to update
     * @param currentHP     the new current hit points value
     * @return the number of rows affected
     */

    public int updateCurrentHealth(UUID charUuid, int currentHP) {
        String sqlText = sql.get("update_current_health");
        var params = new MapSqlParameterSource()
            .addValue("currentHP", currentHP)
            .addValue("uuid", charUuid);
        
        return jdbc.update(sqlText, params);
    }


    /**
     * Updates the temporary hit points for a character.
     * 
     * @param charUuid      the UUID of the character to update
     * @param tempHP        the new temp hit points value
     * @return the number of rows affected
     */
    public int updateTempHealth(UUID charUuid, int tempHP) {
        String sqlText = sql.get("update_temp_health");
        var params = new MapSqlParameterSource()
            .addValue("temp_hp", tempHP)
            .addValue("uuid", charUuid);
        
        return jdbc.update(sqlText, params);
    }

    /**
     * Updates the number of remaining hit dice for a class a 
     * character is associated with.
     * 
     * @param charUuid      the UUID of the character to update
     * @param classUuid     the UUId of the class whose hit dice is being update.
     * @param hitDice       the new remaining hit dice
     * @return the number of rows affected
     */

    public int updateHitDice(UUID charUuid, UUID classUuid, int hitDice) {
        String sqlText = sql.get("update_hit_dice");
        var params = new MapSqlParameterSource()
            .addValue("hit_dice",hitDice)
            .addValue("class_uuid", classUuid)
            .addValue("uuid", charUuid);
        return jdbc.update(sqlText, params);
    }

    /**
     * Updates the name of a character.
     * 
     * @param charUuid      the UUID of the character to update
     * @param name          the new name of the character
     * @return the number of rows affected
     */

    public int updateName(UUID charUuid, String name) {
        String sqlText = sql.get("update_name");
        var params = new MapSqlParameterSource()
            .addValue("name", name)
            .addValue("uuid", charUuid);
        return jdbc.update(sqlText, params);
    }

    /**
     * Update the number of succesful death saving throws of a character.
     * 
     * @param charUuid      the UUID of the character to update
     * @param success       the new count of successful death saves
     * @return the number of rows affected
     */

    public int updateSuccessST(UUID charUuid, int success) {
        String sqlText = sql.get("update_success_st");
        var params = new MapSqlParameterSource()
            .addValue("success", success)
            .addValue("uuid", charUuid);
        return jdbc.update(sqlText, params);
    }

    /**
     * Update the number of failed death saving throws of a character.
     * 
     * @param charUuid      the UUID of the character to update
     * @param failure       the new count of failed death saves
     * @return the number of rows affected
     */

    public int updateFailureST(UUID charUuid, int failure) {
        String sqlText = sql.get("update_failure_st");
        var params = new MapSqlParameterSource()
            .addValue("failure", failure)
            .addValue("uuid", charUuid);
        return jdbc.update(sqlText, params);
    }

    /**
     * Updates an ability score attribute for a character.
     * 
     * Determiens the database column from the provided {@link AbilityScore}
     * and perfomes a direct SQL update.
     * 
     * @param charUuid      the UUID of the character to update
     * @param value         the new ability score value
     * @param as            the {@link AbilityScore} enum indicating which score to update
     * @return the number of rows affected
     */

    public int updateAbilityScore(UUID charUuid, int value, AbilityScore as) {
        String column = as.getString();
        String sql = "UPDATE characters_info "
               + "SET " + column + " = :value "
               + "WHERE char_info_uuid = :uuid";
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("value", value)
            .addValue("uuid",  charUuid);
        return jdbc.update(sql, params);
    }

    /**
     * Updates the inspiration indicator for a character.
     * 
     * 
     * @param charUuid      the UUID of the character to update
     * @param inspiration   the new inspiration value
     * @return the number of rows affected
     */
    public int updateInspiration(UUID charUuid, boolean inspiration) {
        String sqlText = sql.get("update_inspiration");
        var params = new MapSqlParameterSource()
            .addValue("inspiration", inspiration)
            .addValue("uuid", charUuid);

        return jdbc.update(sqlText, params);
    }

    /**
     * Retrives the basic info view for a character, including
     * ability scores, classes, HP handler, and death saving throws.
     * 
     * Returns null if the query fails or no record is found
     * 
     * @param uuid      the UUID of teh charcter to fetch
     * @return a {@link CharcterBasicInfoView} with aggregated data,
     *          or null if retrival fails.
     */        

    public CharacterBasicInfoView getCharInfo(UUID uuid) {
        Map<String, Object> base;
        try {
            base = jdbc.queryForMap(
            sql.get("get_char_info"),
            Map.of("uuid", uuid)
        );

        Map<AbilityScore, Integer> abilityScores = new EnumMap<>(AbilityScore.class);
        for(AbilityScore as : AbilityScore.values()) {
            abilityScores.put(as, (Integer) base.get(as.getString()));
        }

        List<DndClass> classes = jdbc.query(
            sql.get("get_character_classes"),
            Map.of("uuid", uuid),
            (rs, rowNum) -> new DndClass(
                UUID.fromString(rs.getString("class_uuid")),
                rs.getString("class_name"),
                (UUID) rs.getObject("subclass_uuid", UUID.class),
                rs.getString("subclass_name"),
                (short) rs.getInt("level"),
                (short) rs.getInt("remaining_hit_dice"),
                (short) rs.getInt("hit_dice_value")
            )
        );

        HPHandler hpHandler = jdbc.queryForObject(
            sql.get("get_hp_handler"),
            Map.of("uuid",uuid),
            (rs, rowNum) -> new HPHandler(
                rs.getInt("current_hp"),
                rs.getInt("max_hp"),
                rs.getInt("temp_hp")
            )
        );

        DeathSavingThrowsHelper dst = jdbc.queryForObject(
            sql.get("get_death_saving_throws"),
            Map.of("uuid", uuid),
            (rs, rowNum) -> new DeathSavingThrowsHelper(
                (short) rs.getInt("success"),
                (short) rs.getInt("failure")
            )
        );

        return new CharacterBasicInfoView(
            uuid,
            (String) base.get("name"),
            (Boolean) base.get("inspiration"),
            (String) base.get("background_name"),
            UUID.fromString(base.get("background_uuid").toString()),
            (String) base.get("race_name"),
            UUID.fromString(base.get("race_uuid").toString()),
            abilityScores,
            classes,
            hpHandler,
            dst
        );
        } catch (Exception e) {
            return null;
        }
        
    }


}
