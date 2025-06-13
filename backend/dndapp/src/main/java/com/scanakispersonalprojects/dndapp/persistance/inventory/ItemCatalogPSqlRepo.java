package com.scanakispersonalprojects.dndapp.persistance.inventory;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scanakispersonalprojects.dndapp.config.SqlFileLoader;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.RollType;
import com.scanakispersonalprojects.dndapp.model.inventory.EquipableType;
import com.scanakispersonalprojects.dndapp.model.inventory.Item;
import com.scanakispersonalprojects.dndapp.model.inventory.ItemPreview;
import com.scanakispersonalprojects.dndapp.model.inventory.Rarity;
import com.scanakispersonalprojects.dndapp.model.inventory.Skill;
import com.scanakispersonalprojects.dndapp.model.inventory.components.ArmorComponent;
import com.scanakispersonalprojects.dndapp.model.inventory.components.ClassRequirmentComponent;
import com.scanakispersonalprojects.dndapp.model.inventory.components.EquippableComponent;
import com.scanakispersonalprojects.dndapp.model.inventory.components.SkillRequirmentComponent;
import com.scanakispersonalprojects.dndapp.model.inventory.components.SkillAlteredComponent;

@Repository
public class ItemCatalogPSqlRepo implements ItemCatalogRepo {
    
    private final NamedParameterJdbcTemplate jdbc;
    private final SqlFileLoader sql;
    private static ObjectMapper objectMapper = new ObjectMapper();

    public ItemCatalogPSqlRepo(NamedParameterJdbcTemplate jdbc, SqlFileLoader sql) {
        this.jdbc = jdbc;
        this.sql = sql;
    }

    @Override
    public List<ItemPreview> getAll() {
        
        try {
            return jdbc.query(sql.get("get_all_item_catalog"),  new RowMapper<ItemPreview>() {
                
                @Override
                public ItemPreview mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ItemPreview item = new ItemPreview(
                        UUID.fromString(rs.getString("item_uuid")),
                        rs.getString("item_name"),
                        rs.getInt("item_weight"),
                        rs.getInt("item_value"),
                        Rarity.valueOf(rs.getString("item_rarity").toUpperCase())
                    );
                    return item;
                }
            });
    
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }

    @Override
    public Item getItem(UUID itemUuid) {

        Map<String, Object> base;
        try {
            base = jdbc.queryForMap(sql.get("get_item_from_catalog_uuid"), Map.of("item_uuid", itemUuid));

            Item item =  new Item(
            itemUuid,
            (String) base.get("item_name"),
            (String) base.get("item_description"), 
            (int) base.get("item_weight"),
            (int) base.get("item_value"),
            Rarity.valueOf(base.get("item_rarity").toString().toUpperCase()),
            (boolean) base.get("attackable"));

            if(base.get("ac_bonus") != null || base.get("add_as_to_ac") != null) {
                
                Map<AbilityScore, Boolean> addAsToAc = null;

                if(base.get("ac_bonus") != null) {
                    String jsonString = base.get("add_as_to_ac").toString();
                    if(jsonString != null) {
                        Map<String,Boolean> stringMap = parseJsonMap(jsonString, new TypeReference<Map<String, Boolean>>(){});
                        try {
                            addAsToAc = stringMap.entrySet().stream()
                            .collect(Collectors.toMap(
                            entry -> AbilityScore.valueOf(entry.getKey().toUpperCase()),
                            Map.Entry::getValue
                            ));
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                        
                    }
                }

                item.setArmorComponent(new ArmorComponent((short) base.get("ac_bonus"), addAsToAc));    
            }

            if(base.get("equippable") != null || base.get("attunable") != null || base.get("item_equippable_type") != null) {

                List<EquipableType> equipableTypes = null;
                if(base.get("item_equippable_type") != null) {

                    Array sqlArray = (Array)base.get("item_equippable_type");
                    Object[] typeObjects = (Object[])sqlArray.getArray();

                    try {
                        equipableTypes = Arrays.stream(typeObjects)
                        .map(typeObject -> EquipableType.valueOf(typeObject.toString().toUpperCase()))
                            .collect(Collectors.toList());
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    

                }

                item.setEquippableComponent(new EquippableComponent(
                    (boolean)base.get("equippable"),
                    (boolean) base.get("attunable"), 
                    equipableTypes));
            }

            if(base.get("ability_requirment") != null) {
                
                String jsonString = base.get("ability_requirment").toString();
                Map<AbilityScore, Integer> abilityRequirment = null;
                if(jsonString != null) {
                    
                    Map<String,Integer> stringMap = parseJsonMap(jsonString, new TypeReference<Map<String, Integer>>(){});
                    
                    try {
                        abilityRequirment = stringMap.entrySet().stream()
                        .collect(Collectors.toMap(
                            entry -> AbilityScore.valueOf(entry.getKey().toUpperCase()),
                            Map.Entry::getValue
                        ));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    
                }

                item.setRequirmentComponent(new SkillRequirmentComponent(abilityRequirment));
            }

            if(base.get("skill_altered_roll_type") != null || base.get("skill_altered_bonus") != null) {
                
                Map<Skill, RollType> skillAlteredRollType = null;
                if(base.get("skill_altered_roll_type") != null) {
                    String jsonString = base.get("skill_altered_roll_type").toString();                    
                    if(jsonString != null) {
                        Map<String,String> stringMap = parseJsonMap(jsonString, new TypeReference<Map<String, String>>(){});
                        
                        try {
                        skillAlteredRollType = stringMap.entrySet().stream()
                            .collect(Collectors.toMap(
                                entry -> Skill.valueOf(entry.getKey().toUpperCase()), 
                                entry -> RollType.valueOf(entry.getValue().toUpperCase()))); 
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                        
                    }
                
                }
                
                Map<Skill, Integer> skillAlteredBonus = null;
                if(base.get("skill_altered_bonus") != null) {
                    String jsonString = base.get("skill_altered_bonus").toString();

                    if(jsonString != null) {
                        Map<String,Integer> stringMap = parseJsonMap(jsonString, new TypeReference<Map<String, Integer>>(){});
                        try {
                            skillAlteredBonus = stringMap.entrySet().stream()
                            .collect(Collectors.toMap(
                                entry -> Skill.valueOf(entry.getKey().toUpperCase()), 
                                Map.Entry::getValue));
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    
                    }
                }
                item.setSkillAlteredComponent(new SkillAlteredComponent(skillAlteredRollType, skillAlteredBonus));
                
            }

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("item_uuid", itemUuid);

            List<Map<String, Object>> classRequirmentList = jdbc.queryForList(sql.get("get_item_class_requirments"), params);

            if(!classRequirmentList.isEmpty()) {
                Map<UUID, String> classRequirments = classRequirmentList.stream()
                .collect(Collectors.toMap(
                    row -> UUID.fromString(row.get("class_uuid").toString()),
                    row -> row.get("name").toString()));
                item.setClassRequirmentComponent(new ClassRequirmentComponent(classRequirments));
            }
            

            return item;


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private <T> Map<String, T> parseJsonMap(String jsonString, TypeReference<Map<String,T >> ref) {
        try {
            return objectMapper.readValue(jsonString, ref);    
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
 
    
    }

    @Override
    public List<ItemPreview> searchByName(String name) {
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("name", name);

        return jdbc.query(sql.get("item_catalog_fuzzy_search"), 
            params,
            new RowMapper<ItemPreview>() {
                @Override
                public ItemPreview mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ItemPreview item = new ItemPreview(
                        UUID.fromString(rs.getString("item_uuid")),
                        rs.getString("item_name"),
                        rs.getInt("item_weight"),
                        rs.getInt("item_value"),
                        Rarity.valueOf(rs.getString("item_rarity").toUpperCase())
                    );
                    return item;
                }
            });
    }

}
