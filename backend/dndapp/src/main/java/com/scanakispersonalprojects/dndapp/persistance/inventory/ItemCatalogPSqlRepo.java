package com.scanakispersonalprojects.dndapp.persistance.inventory;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;


import com.scanakispersonalprojects.dndapp.config.SqlFileLoader;
import com.scanakispersonalprojects.dndapp.model.inventory.Item;
import com.scanakispersonalprojects.dndapp.model.inventory.ItemPreview;

public class ItemCatalogPSqlRepo implements ItemCatalogRepo {
    
    private final NamedParameterJdbcTemplate jdbc;
    private final SqlFileLoader sql;

    public ItemCatalogPSqlRepo(NamedParameterJdbcTemplate jdbc, SqlFileLoader sql) {
        this.jdbc = jdbc;
        this.sql = sql;
    }

    @Override
    public List<ItemPreview> getAll() {
        try {
            return jdbc.query(sql.get("get_all_item_catalog"), new BeanPropertyRowMapper<>(ItemPreview.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        
    }

    @Override
    public Item getItem(UUID itemUuid) {

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("itemUuid", itemUuid);

        return jdbc.queryForObject(sql.get("get_item_from_catalog_uuid"), 
            params, 
            new BeanPropertyRowMapper<>(Item.class)
        );
    }

    @Override
    public List<ItemPreview> searchByName(String name) {
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("name", name);

        return jdbc.query(sql.get("item_catalog_fuzzy_search"), 
            params,
            new BeanPropertyRowMapper<>(ItemPreview.class));
    }

}
