package com.scanakispersonalprojects.dndapp.model.basicCharInfo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

@SpringBootTest
public class SubClassUnitTest {
    
    private final UUID SUBCLASS_UUID = UUID.randomUUID();
    private final String NAME = "testSub";
    private final UUID CLASS_SOURCE = UUID.randomUUID();
    private final DndClass PARENT_CLASS = new DndClass(CLASS_SOURCE, "Fighter", HitDiceValue.D10, "A master of martial combat.");
    
    private Subclass subclass;
    
    @BeforeEach
    void setUp() {
        subclass = new Subclass();
    }
    
    @Test
    void testDefaultConstructor() {
        Subclass defaultSubclass = new Subclass();
        
        assertNull(defaultSubclass.getSubclassUuid());
        assertNull(defaultSubclass.getName());
        assertNull(defaultSubclass.getClassSource());
        assertNull(defaultSubclass.getParentClass());
    }
    
    @Test
    void testTwoParameterConstructor() {
        Subclass twoParamSubclass = new Subclass(NAME, CLASS_SOURCE);
        
        assertNull(twoParamSubclass.getSubclassUuid());
        assertEquals(NAME, twoParamSubclass.getName());
        assertEquals(CLASS_SOURCE, twoParamSubclass.getClassSource());
        assertNull(twoParamSubclass.getParentClass());
    }
    
    @Test
    void testThreeParameterConstructor() {
        Subclass threeParamSubclass = new Subclass(SUBCLASS_UUID, NAME, CLASS_SOURCE);
        
        assertEquals(SUBCLASS_UUID, threeParamSubclass.getSubclassUuid());
        assertEquals(NAME, threeParamSubclass.getName());
        assertEquals(CLASS_SOURCE, threeParamSubclass.getClassSource());
        assertNull(threeParamSubclass.getParentClass());
    }
    
    @Test
    void testGetAndSetSubclassUuid() {
        subclass.setSubclassUuid(SUBCLASS_UUID);
        assertEquals(SUBCLASS_UUID, subclass.getSubclassUuid());
    }
    
    @Test
    void testGetAndSetName() {
        subclass.setName(NAME);
        assertEquals(NAME, subclass.getName());
    }
    
    @Test
    void testGetAndSetClassSource() {
        subclass.setClassSource(CLASS_SOURCE);
        assertEquals(CLASS_SOURCE, subclass.getClassSource());
    }
    
    @Test
    void testGetAndSetParentClass() {
        subclass.setParentClass(PARENT_CLASS);
        assertEquals(PARENT_CLASS, subclass.getParentClass());
    }
    
    @Test
    void testGetFullNameWithParentClass() {
        subclass.setName(NAME);
        subclass.setParentClass(PARENT_CLASS);
        
        String expectedFullName = PARENT_CLASS.getName() + " - " + NAME;
        assertEquals(expectedFullName, subclass.getFullName());
    }
    
    @Test
    void testGetFullNameWithoutParentClass() {
        subclass.setName(NAME);
        subclass.setParentClass(null);
        
        assertEquals(NAME, subclass.getFullName());
    }
    
    @Test
    void testGetFullNameWithNullName() {
        subclass.setName(null);
        subclass.setParentClass(PARENT_CLASS);
        
        String expectedFullName = PARENT_CLASS.getName() + " - " + null;
        assertEquals(expectedFullName, subclass.getFullName());
    }
    
    @Test
    void testGetFullNameWithNullNameAndNoParent() {
        subclass.setName(null);
        subclass.setParentClass(null);
        
        assertNull(subclass.getFullName());
    }
    
    @Test
    void testEqualsWithSameObject() {
        assertTrue(subclass.equals(subclass));
    }
    
    @Test
    void testEqualsWithNull() {
        assertFalse(subclass.equals(null));
    }
    
    @Test
    void testEqualsWithSameUuid() {
        Subclass subclass1 = new Subclass(SUBCLASS_UUID, NAME, CLASS_SOURCE);
        Subclass subclass2 = new Subclass(SUBCLASS_UUID, "Different Name", UUID.randomUUID());
        
        assertTrue(subclass1.equals(subclass2));
    }
    
    @Test
    void testEqualsWithDifferentUuid() {
        Subclass subclass1 = new Subclass(SUBCLASS_UUID, NAME, CLASS_SOURCE);
        Subclass subclass2 = new Subclass(UUID.randomUUID(), NAME, CLASS_SOURCE);
        
        assertFalse(subclass1.equals(subclass2));
    }
    
    @Test
    void testEqualsWithNullUuids() {
        Subclass subclass1 = new Subclass(NAME, CLASS_SOURCE);
        Subclass subclass2 = new Subclass(NAME, CLASS_SOURCE);
        
        assertFalse(subclass1.equals(subclass2));
    }
    
    @Test
    void testEqualsWithOneNullUuid() {
        Subclass subclass1 = new Subclass(SUBCLASS_UUID, NAME, CLASS_SOURCE);
        Subclass subclass2 = new Subclass(NAME, CLASS_SOURCE);
        
        assertFalse(subclass1.equals(subclass2));
    }
    
    @Test
    void testHashCode() {
        Subclass subclass1 = new Subclass(SUBCLASS_UUID, NAME, CLASS_SOURCE);
        Subclass subclass2 = new Subclass(SUBCLASS_UUID, "Different Name", UUID.randomUUID());
        
        assertEquals(subclass1.hashCode(), subclass2.hashCode());
    }

    
}