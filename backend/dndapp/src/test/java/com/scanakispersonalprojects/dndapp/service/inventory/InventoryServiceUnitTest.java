package com.scanakispersonalprojects.dndapp.service.inventory;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.scanakispersonalprojects.dndapp.model.inventory.CharacterHasItemProjection;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Testcontainers
public class InventoryServiceUnitTest {
    
    @Autowired
    private InventoryService inventoryService;
    private final UUID thorinUuid = UUID.fromString("eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9");
     private final UUID longSwordUuid = UUID.fromString("aaaa0000-0000-0000-0000-000000000001");


    @Test
    public void getInventoryUsingUUID_returnsThorin() {
        List<CharacterHasItemProjection> items =  inventoryService.getInventoryWithUUID(thorinUuid);
        
        assertEquals(3, items.size());
        assertEquals(items.get(0).getItemName(), "Chain Mail");
        assertEquals(items.get(1).getItemName(), "Battleaxe");
        assertEquals(items.get(2).getItemName(), "Healing Potion");
        
        assertEquals(items.get(0).getAttuned(), false);
        assertEquals(items.get(1).getAttuned(), false);
        assertEquals(items.get(2).getAttuned(), false);
        
        assertEquals(items.get(0).getEquipped(), true);
        assertEquals(items.get(1).getEquipped(), true);
        assertEquals(items.get(2).getEquipped(), false);

        assertEquals((int)items.get(0).getQuantity(), 1);
        assertEquals((int)items.get(1).getQuantity(), 1);
        assertEquals((int)items.get(2).getQuantity(), 3);
    }

    @Test
    public void getInventoryUsingUUID_returnsNull() throws Exception{
        List<CharacterHasItemProjection> items = inventoryService.getInventoryWithUUID(UUID.randomUUID());
        
        assertEquals(0, items.size());
    }

    @Test
    public void getInventoryUsingFZF_returnsChainMail() {
        List<CharacterHasItemProjection> items =  inventoryService.getInventoryUsingFZF(thorinUuid, "Chain Mail");
        
        assertEquals(1, items.size());
        assertEquals(items.get(0).getItemName(), "Chain Mail");
        assertEquals(items.get(0).getAttuned(), false);
        assertEquals(items.get(0).getEquipped(), true);
        assertEquals((int)items.get(0).getQuantity(), 1);
    }

    @Test
    public void getInventoryUsingFZF_returnsNothing() throws Exception{
        List<CharacterHasItemProjection> items =  inventoryService.getInventoryUsingFZF(thorinUuid, "@asldkfjal;kjw");
        
        assertEquals(0, items.size());
    }

    @Test
    public void saveItemToInventory_success() throws Exception{
        List<CharacterHasItemProjection> items = inventoryService.getInventoryWithUUID(thorinUuid);
        assertEquals(3, items.size());

        inventoryService.saveItemToInventory(longSwordUuid, thorinUuid, 3);
        items = inventoryService.getInventoryWithUUID(thorinUuid);
        assertEquals(4, items.size());
        assertEquals(items.get(0).getItemName(), "Longsword");
    }

}
