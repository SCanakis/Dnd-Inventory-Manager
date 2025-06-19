package com.scanakispersonalprojects.dndapp.service.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ClassNameIdPair;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.EquippableType;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ItemCatalog;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ItemProjection;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.Rarity;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Testcontainers
public class ItemCatalogServiceUnitTest {

    @Autowired
    private ItemCatalogService itemCatalogService;

    private final UUID swordUuid = UUID.fromString("aaaa0000-0000-0000-0000-000000000001");
    private final ItemCatalog sword = new ItemCatalog(
        swordUuid,
        "Longsword",
        "A versatile martial weapon with a straight double-edged blade.",
        3,
        15,
        Rarity.common,
        true,
        null,
        null,
        true,
        false,
        new ArrayList<EquippableType>() {{
            add(EquippableType.mainhand);
            add(EquippableType.offhand);
            add(EquippableType.twohand);
        }},
        null,
        null,
        null,
        false,
        null,
        new ArrayList<>() {{
            add(new ClassNameIdPair(
                UUID.fromString("c1a55000-0000-0000-0000-000000000001"),
                "Fighter"
            ));
            add(new ClassNameIdPair(
                UUID.fromString("c1a55000-0000-0000-0000-000000000006"),
                "Paladin"
            ));
            add(new ClassNameIdPair(
                UUID.fromString("c1a55000-0000-0000-0000-000000000005"),
                "Ranger"
            ));
        }}
    );


    @Test
    public void getItemWithUUID_returnsItem() throws Exception{
        ItemCatalog actualSword = itemCatalogService.getItemWithUUID(swordUuid);

        assertTrue(sword.equals(actualSword));
        assertEquals(sword.getItemUuid(), actualSword.getItemUuid());
        assertEquals(sword.getItemName(), actualSword.getItemName());
        assertEquals(sword.getItemWeight(), actualSword.getItemWeight());
        assertEquals(sword.getItemValue(), actualSword.getItemValue());
        assertEquals(sword.getItemRarity(), actualSword.getItemRarity());
        assertEquals(sword.isAttackable(), actualSword.isAttackable());
        assertEquals(sword.isAttunable(), actualSword.isAttunable());
        assertEquals(sword.isEquippable(), actualSword.isEquippable());
        assertEquals(sword.getAcBonus(), actualSword.getAcBonus());
        assertEquals(sword.getAddToAc(), actualSword.getAddToAc());
        assertEquals(sword.getItemEquippableTypes(), actualSword.getItemEquippableTypes());
        assertEquals(sword.getAbilityRequirement(), actualSword.getAbilityRequirement());
        assertEquals(sword.getSkillAlteredBonus(), actualSword.getSkillAlteredBonus());
        assertNotNull(actualSword.getClassNameIdPair());
        assertEquals(3, actualSword.getClassNameIdPair().size());
    }

    @Test
    public void getItemWithUUID_returnsNull() throws Exception {
        ItemCatalog actualSword = itemCatalogService.getItemWithUUID(UUID.randomUUID());
    
        assertNull(actualSword);
    }

    @Test
    public void getAll() throws Exception {
        List<ItemProjection> items = itemCatalogService.getAll();
        assertEquals(26, items.size());
    }

    @Test
    public void searchByName_returnsItems() throws Exception {
        List<ItemProjection> items = itemCatalogService.searchByName("sword");
        assertEquals(6, items.size());
        assertEquals("Longsword", items.get(1).getItemName());
        assertEquals(swordUuid, items.get(1).getItemUuid());
    }

    @Test
    public void searchByName_returnsEmpty() throws Exception {
        List<ItemProjection> items = itemCatalogService.searchByName("@asldkjf092kcxmzx");
        assertEquals(0, items.size());
    }

}
