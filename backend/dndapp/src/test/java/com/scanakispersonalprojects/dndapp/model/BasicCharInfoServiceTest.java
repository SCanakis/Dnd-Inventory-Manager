package com.scanakispersonalprojects.dndapp.model;


import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BasicCharInfoServiceTest {
    
    @Test
    public void CharViewPatchNulls() {
        CharViewPatch patch = new CharViewPatch(null, null, null, null, null, null, null, null);

        assertNull(patch.name());
        assertEquals(patch.currentHP(), -1);
        assertEquals(patch.tempHP(), -1);
        assertEquals(patch.success(), -1);
        assertEquals(patch.failure(), -1);
        assertTrue(patch.hitDice().isEmpty());
        assertNull(patch.inspiration());
    }

}
