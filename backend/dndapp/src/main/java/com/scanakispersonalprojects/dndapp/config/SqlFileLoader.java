package com.scanakispersonalprojects.dndapp.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import jakarta.annotation.PostConstruct;



/**
 * This Component is responsible for loading all SQL files from teh classpath under 
 * sql and making thier contents availabe by filename key.
 * 
 * On application startup, scans the files matching "classpath:sql/*.sql" and caches
 * each file's contents in memory. Consumers can then retrieve the SQL text by
 * calling get(String) with the filenmae. 
 */


@Component
public class SqlFileLoader {
    
    /**
     * Scans the classpath for sql/*.sql resouces and load each
     * file's contents into the internal cache.
     * 
     * This method is invoked automatically after construciton because of
     * {@link PostContruct} annotation.
     * 
     */
    private final Map<String, String> sql = new HashMap<>();

    @PostConstruct
    public void loadSqlFiles() throws IOException {
        var resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:sql/*/*.sql");

        for(Resource r : resources) {
            if(r != null && r.getFilename() != null) {
                String filename = r.getFilename();
                String key = filename.substring(0, filename.lastIndexOf("."));
                String sqlText = StreamUtils.copyToString(r.getInputStream(), StandardCharsets.UTF_8);
                sql.put(key, sqlText);
            }
        }
    }

    /**
     * Retrieves the SQL text associated with the given key.
     * 
     * @param key
     * @return
     */
    public String get(String key) {
        return sql.get(key);
    }

}
