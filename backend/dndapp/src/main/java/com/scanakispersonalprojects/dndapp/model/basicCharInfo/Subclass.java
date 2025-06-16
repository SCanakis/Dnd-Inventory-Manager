package com.scanakispersonalprojects.dndapp.model.basicCharInfo;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "subclass")
public class Subclass {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "subclass_uuid", columnDefinition = "UUID")
    private UUID subclassUuid;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    @Column(name = "class_source", nullable = false, columnDefinition = "UUID")
    private UUID classSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_source", insertable = false, updatable = false)
    private DndClass parentClass;

    public Subclass() {}

    public Subclass(String name, UUID classSource) {
        this.name = name;
        this.classSource = classSource;
    }

    public Subclass(UUID subclassUuid, String name, UUID classSource) {
        this.subclassUuid = subclassUuid;
        this.name = name;
        this.classSource = classSource;
    }

    public UUID getSubclassUuid() {
        return subclassUuid;
    }

    public void setSubclassUuid(UUID subclassUuid) {
        this.subclassUuid = subclassUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getClassSource() {
        return classSource;
    }

    public void setClassSource(UUID classSource) {
        this.classSource = classSource;
    }

    public DndClass getParentClass() {
        return parentClass;
    }

    public void setParentClass(DndClass parentClass) {
        this.parentClass = parentClass;
    }

    public String getFullName() {
        if (parentClass != null) {
            return parentClass.getName() + " - " + name;
        }
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subclass)) return false;
        Subclass subclass = (Subclass) o;
        return subclassUuid != null && subclassUuid.equals(subclass.subclassUuid);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Subclass{" +
                "subclassUuid=" + subclassUuid +
                ", name='" + name + '\'' +
                ", classSource=" + classSource +
                '}';
    }
}
