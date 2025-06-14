package com.scanakispersonalprojects.dndapp.model.inventory;

import java.util.UUID;

public class ClassNameIdPair {
    private UUID classUuid;
    private String className;
    
    public ClassNameIdPair(UUID classUuid, String className) {
        this.classUuid = classUuid;
        this.className = className;
    }

    public UUID getClassUuid() {
        return classUuid;
    }

    public void setClassUuid(UUID classUuid) {
        this.classUuid = classUuid;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    
}
