package com.scanakispersonalprojects.dndapp.model.basicCharInfo;


import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;


@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BasicCharInfoModelUnitTest {
    
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
