package com.scanakispersonalprojects.dndapp.model.inventory.containers;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Entity representing an inventory container owned by a character.
 * Containers can hold items and have capacity limits to manage inventory organization.
 * Examples include bags, chests, backpacks, or any item that can store other items.
 * A character's general inventory is also considered a container
 * 
 * Uses a composite primary key consisting of container UUID and character UUID
 * to uniquely identify each container within a character's inventory.
 * 
 * pk       container_uuid      UUID
 * pk       character_uuid      UUID
 *          item_uuid           UUID
 *          max_capactiy        INT
 *          current_capacity    INTA
 * 
 */
@Entity
@Table(name = "container")
public class Container {
    
    @EmbeddedId
    private ContainerId id;

    /** Reference to the item in the catalog that represents this container */
    @Column(name = "item_uuid", columnDefinition = "UUID")
    private UUID itemUuid;

    @Column(name = "max_capacity", nullable = false) 
    private Integer maxCapacity;
    
    @Column(name = "current_consumed", nullable =  false) 
    private Double currentCapacity = 0.0;

    public Container() {
    }

    public Container(UUID containerUuid, UUID charUuid, UUID itemUuid, int maxCapacity, double currentCapacity) {
        this.id = new ContainerId(containerUuid, charUuid);
        this.itemUuid = itemUuid;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
    }

    public ContainerId getId() {
        return id;
    }

    public UUID getCharUuid() {
        if(id == null) {
            return null;
        }
        return id.getCharUuid();
    }

    public void setCharUuid(UUID charUuid) {
        if(this.id == null) {
            this.id = new ContainerId();
        }
        this.id.setCharUuid(charUuid);
    }

    public UUID getContainerUuid() {
        if(id == null) {
            return null;
        }
        return id.getContainerUuid();
    }

    public void setContainerUuid(UUID containerUuid) {
        if(this.id == null) {
            this.id = new ContainerId();
        }
        this.id.setContainerUuid(containerUuid);
    }


    public void setId(ContainerId id) {
        this.id = id;
    }

    public UUID getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(UUID itemUuid) {
        this.itemUuid = itemUuid;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public double getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(double currentCapacity) {
        this.currentCapacity = currentCapacity;
    }



}
