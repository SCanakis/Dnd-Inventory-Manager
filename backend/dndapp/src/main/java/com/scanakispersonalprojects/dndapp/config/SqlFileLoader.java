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

@Component
public class SqlFileLoader {
    
    private final Map<String, String> sql = new HashMap<>();

    @PostConstruct
    public void loadSqlFiles() throws IOException {
        var resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:sql/*.sql");

        for(Resource r : resources) {

            String filename = r.getFilename();
            String key = filename.substring(0, filename.lastIndexOf("."));
            String sqlText = StreamUtils.copyToString(r.getInputStream(), StandardCharsets.UTF_8);
            sql.put(key, sqlText);
        }
    }

    public String get(String key) {
        return sql.get(key);
    }

}
