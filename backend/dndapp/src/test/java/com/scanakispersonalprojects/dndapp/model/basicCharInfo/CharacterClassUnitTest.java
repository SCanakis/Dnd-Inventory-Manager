package com.scanakispersonalprojects.dndapp.model.basicCharInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CharacterClassUnitTest {
    
    private final UUID CHAR_INFO_UUID = UUID.randomUUID();
    private final UUID CLASS_UUID = UUID.randomUUID();

    private final CharacterHasClassId CHAR_CLASS_ID = new CharacterHasClassId(CHAR_INFO_UUID, CLASS_UUID);

    private final UUID SUBCLASS_UUID = UUID.randomUUID();
    private final short LEVEL = 5;
    private final short HIT_DICE_REMAINING = 4;


    @Test
    public void constructor1() {
        CharacterClass cc = new CharacterClass();
        
        assertNull(cc.getId());
        assertNull(cc.getClassUuid());
        assertNull(cc.getSubclassUuid());
        assertEquals((short)cc.getLevel(), 1);
        assertNull(cc.getHitDiceRemaining());
    }

    @Test
    public void constructor2() {
        CharacterClass cc = new CharacterClass(CHAR_INFO_UUID, CLASS_UUID, LEVEL, HIT_DICE_REMAINING);
        
        assertEquals(cc.getId().getCharInfoUuid(),CHAR_INFO_UUID);
        assertEquals(cc.getId().getClassUuid(),CLASS_UUID);
        assertEquals(cc.getClassUuid(), CLASS_UUID);
        assertEquals((short)cc.getLevel(), LEVEL);
        assertEquals((short)cc.getHitDiceRemaining(), HIT_DICE_REMAINING);
    }

    @Test
    public void constructor3() {
        CharacterClass cc = new CharacterClass(CHAR_INFO_UUID, CLASS_UUID, SUBCLASS_UUID,LEVEL, HIT_DICE_REMAINING);
        
        assertEquals(cc.getId().getCharInfoUuid(),CHAR_INFO_UUID);
        assertEquals(cc.getId().getClassUuid(),CLASS_UUID);
        assertEquals(cc.getClassUuid(), CLASS_UUID);
        assertEquals(cc.getSubclassUuid(), SUBCLASS_UUID);
        assertEquals((short)cc.getLevel(), LEVEL);
        assertEquals((short)cc.getHitDiceRemaining(), HIT_DICE_REMAINING);
    }

    @Test
    public void setters() {
        CharacterClass cc = new CharacterClass();
        cc.setId(CHAR_CLASS_ID);
        cc.setClassUuid(CLASS_UUID);
        cc.setSubclassUuid(SUBCLASS_UUID);
        cc.setLevel(LEVEL);
        cc.setHitDiceRemaining(HIT_DICE_REMAINING);

        assertEquals(cc.getId(),CHAR_CLASS_ID);
        assertEquals(cc.getClassUuid(), CLASS_UUID);
        assertEquals(cc.getSubclassUuid(), SUBCLASS_UUID);
        assertEquals((short)cc.getLevel(), LEVEL);
        assertEquals((short)cc.getHitDiceRemaining(), HIT_DICE_REMAINING);
    }



}
