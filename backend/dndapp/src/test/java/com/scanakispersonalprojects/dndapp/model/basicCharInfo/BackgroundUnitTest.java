package com.scanakispersonalprojects.dndapp.model.basicCharInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BackgroundUnitTest {

    private final UUID BG_UUID = UUID.randomUUID();
    private final String BG_NAME = "aTestBackground";
    private final String BG_DESC = "This is a test Background";
    private final short BG_MONEY = 200;

    
    @Test
    public void constructors_getters1() {
        Background bg = new Background(BG_NAME, BG_DESC, (short) 200);
        
        assertNull(bg.getBackgroundUuid());
        assertEquals(BG_NAME, bg.getName());
        assertEquals(BG_DESC, bg.getDescription());
        assertEquals(BG_MONEY, (short) bg.getStartingGold());
    } 
    
    @Test
    public void constructors_getters2() {
        Background bg = new Background(BG_UUID, BG_NAME, BG_DESC, (short) 200);
        
        assertEquals(BG_UUID, bg.getBackgroundUuid());
        assertEquals(BG_NAME, bg.getName());
        assertEquals(BG_DESC, bg.getDescription());
        assertEquals(BG_MONEY, (short) bg.getStartingGold());
    }

    @Test
    public void constructors_setters2() {
        Background bg = new Background(BG_UUID, BG_NAME, BG_DESC, (short) 200);
        
        assertEquals(BG_UUID, bg.getBackgroundUuid());
        assertEquals(BG_NAME, bg.getName());
        assertEquals(BG_DESC, bg.getDescription());
        assertEquals(BG_MONEY, (short) bg.getStartingGold());

        UUID randomUuid = UUID.randomUUID();
        String randomName = "asdf";
        String randomDescription = "asdf";
        short randomMoney = 123;
        bg.setBackgroundUuid(randomUuid);
        bg.setName(randomName);
        bg.setDescription(randomDescription);
        bg.setStartingGold(randomMoney);

        assertEquals(randomUuid, bg.getBackgroundUuid());
        assertEquals(randomName, bg.getName());
        assertEquals(randomDescription, bg.getDescription());
        assertEquals(randomMoney, (short)bg.getStartingGold()); 
    } 

    @Test
    public void equalsTester() {
        Background bg = new Background(BG_UUID, BG_NAME, BG_DESC, (short) 200);
        Background bg2 = new Background(BG_UUID, "NotBg", "aDescirtipon", (short) 0);
        assertEquals(bg, bg2);
    }


}
