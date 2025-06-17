package com.scanakispersonalprojects.dndapp.persistance.basicCharInfo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Background;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterInfo;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.DeathSavingThrowsHelper;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.HPHandler;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Race;

@DataJpaTest
@ActiveProfiles("test")
class BasicCharInfoRepositoryUnitTest {

    @Autowired
    private CharacterInfoRepo characterInfoRepo;

    @Autowired
    private RaceRepo raceRepo;

    @Autowired
    private BackgroundRepo backgroundRepo;

    // Test data
    private CharacterInfo testCharacter;
    private Race testRace;
    private Background testBackground;

    @BeforeEach
    void setUp() {
        // Clean up any existing data
        characterInfoRepo.deleteAll();
        raceRepo.deleteAll();
        backgroundRepo.deleteAll();

        // Create test race
        testRace = new Race();
        testRace.setName("Test Dwarf");
        testRace.setStatIncreases(Map.of(
            AbilityScore.strength, 1,
            AbilityScore.constitution, 2
        ));
        testRace = raceRepo.save(testRace);

        // Create test background
        testBackground = new Background();
        testBackground.setName("Test Soldier");
        testBackground.setDescription("A test background");
        testBackground.setStartingGold((short) 10);
        testBackground = backgroundRepo.save(testBackground);

        // Create test character
        testCharacter = new CharacterInfo();
        testCharacter.setName("Test Hero");
        testCharacter.setInspiration(false);
        testCharacter.setRaceUuid(testRace.getRaceUuid());
        testCharacter.setBackgroundUuid(testBackground.getBackgroundUuid());
        testCharacter.setAbilityScores(Map.of(
            AbilityScore.strength, 16,
            AbilityScore.dexterity, 12,
            AbilityScore.constitution, 15,
            AbilityScore.intelligence, 10,
            AbilityScore.wisdom, 13,
            AbilityScore.charisma, 8
        ));
        testCharacter.setHpHandler(new HPHandler(12, 12, 0));
        testCharacter.setDeathSavingThrowsHelper(new DeathSavingThrowsHelper(0, 0));
        testCharacter = characterInfoRepo.save(testCharacter);
    }

    @Test
    public void testGetCharInfo_Success() {
        // Act
        Optional<CharacterInfo> found = characterInfoRepo.findById(testCharacter.getCharInfoUuid());
        
        // Assert
        assertTrue(found.isPresent());
        CharacterInfo character = found.get();
        
        assertNotNull(character);
        assertEquals("Test Hero", character.getName());
        assertNotNull(character.getRaceUuid());
        assertNotNull(character.getBackgroundUuid());
        
        // Check ability scores
        Map<AbilityScore, Integer> abilityScores = character.getAbilityScores();
        assertNotNull(abilityScores);
        assertEquals(16, abilityScores.get(AbilityScore.strength));
        assertEquals(12, abilityScores.get(AbilityScore.dexterity));
        assertEquals(15, abilityScores.get(AbilityScore.constitution));
    }

    @Test
    public void testGetCharInfo_CharacterNotFound() {
        // Act & Assert
        assertFalse(characterInfoRepo.findById(UUID.randomUUID()).isPresent());
    }

    @Test
    public void testCreateCharacter_WithValidData() {
        // Arrange
        CharacterInfo newCharacter = new CharacterInfo();
        newCharacter.setName("Another Hero");
        newCharacter.setInspiration(true);
        newCharacter.setRaceUuid(testRace.getRaceUuid());
        newCharacter.setBackgroundUuid(testBackground.getBackgroundUuid());
        newCharacter.setAbilityScores(Map.of(
            AbilityScore.strength, 14,
            AbilityScore.dexterity, 16,
            AbilityScore.constitution, 13,
            AbilityScore.intelligence, 12,
            AbilityScore.wisdom, 15,
            AbilityScore.charisma, 10
        ));
        newCharacter.setHpHandler(new HPHandler(10, 8, 2));
        newCharacter.setDeathSavingThrowsHelper(new DeathSavingThrowsHelper(0, 0));
        
        // Act
        CharacterInfo saved = characterInfoRepo.save(newCharacter);
        
        // Assert
        assertNotNull(saved);
        assertNotNull(saved.getCharInfoUuid());
        assertEquals("Another Hero", saved.getName());
        assertTrue(saved.getInspiration());
    }

    @Test
    public void testFindAll_ReturnsAllCharacters() {
        // Act
        List<CharacterInfo> allCharacters = characterInfoRepo.findAll();
        
        // Assert
        assertNotNull(allCharacters);
        assertEquals(1, allCharacters.size());
        assertEquals("Test Hero", allCharacters.get(0).getName());
    }

    @Test
    public void updateCharInfo_Successful() {
        // Arrange
        UUID characterId = testCharacter.getCharInfoUuid();
        
        // Act - Update inspiration and HP
        testCharacter.setInspiration(true);
        HPHandler newHpHandler = new HPHandler(15, 10, 3);
        testCharacter.setHpHandler(newHpHandler);
        
        CharacterInfo updatedCharacter = characterInfoRepo.save(testCharacter);
        
        // Assert
        assertNotNull(updatedCharacter);
        assertEquals(characterId, updatedCharacter.getCharInfoUuid());
        assertTrue(updatedCharacter.getInspiration());
        
        HPHandler hpHandler = updatedCharacter.getHpHandler();
        assertEquals(15, hpHandler.maxHp());
        assertEquals(10, hpHandler.currentHp());
        assertEquals(3, hpHandler.temporaryHp());
        
        // Verify persistence by refetching
        Optional<CharacterInfo> refetched = characterInfoRepo.findById(characterId);
        assertTrue(refetched.isPresent());
        assertTrue(refetched.get().getInspiration());
    }

    @Test
    public void testDeleteCharacter_Success() {
        // Arrange
        UUID characterId = testCharacter.getCharInfoUuid();
        
        // Verify character exists
        assertTrue(characterInfoRepo.existsById(characterId));
        
        // Act - Delete the character
        characterInfoRepo.delete(testCharacter);
        
        // Assert
        assertFalse(characterInfoRepo.existsById(characterId));
        assertFalse(characterInfoRepo.findById(characterId).isPresent());
        
        // Verify total count decreased
        List<CharacterInfo> remainingCharacters = characterInfoRepo.findAll();
        assertEquals(0, remainingCharacters.size());
    }

    @Test
    public void testCharacterWithAbilityScores() {
        // Assert ability scores
        Map<AbilityScore, Integer> abilityScores = testCharacter.getAbilityScores();
        assertNotNull(abilityScores);
        assertEquals(16, abilityScores.get(AbilityScore.strength));
        assertEquals(12, abilityScores.get(AbilityScore.dexterity));
        assertEquals(15, abilityScores.get(AbilityScore.constitution));
        assertEquals(10, abilityScores.get(AbilityScore.intelligence));
        assertEquals(13, abilityScores.get(AbilityScore.wisdom));
        assertEquals(8, abilityScores.get(AbilityScore.charisma));
    }

    @Test
    public void testCharacterHpHandling() {
        // Assert HP handling
        HPHandler hpHandler = testCharacter.getHpHandler();
        assertNotNull(hpHandler);
        assertEquals(12, hpHandler.maxHp());
        assertEquals(12, hpHandler.currentHp());
        assertEquals(0, hpHandler.temporaryHp());
    }

    @Test
    public void testCharacterDeathSavingThrows() {
        // Assert death saving throws structure
        DeathSavingThrowsHelper deathSaves = testCharacter.getDeathSavingThrowsHelper();
        assertNotNull(deathSaves);
        assertEquals(0, deathSaves.successes());
        assertEquals(0, deathSaves.failures());
    }

    @Test
    public void testRaceAndBackgroundUuidsExist() {
        // Assert that UUIDs are present and valid
        assertNotNull(testCharacter.getRaceUuid());
        assertNotNull(testCharacter.getBackgroundUuid());
        
        // Verify the UUIDs actually reference existing records
        assertTrue(raceRepo.existsById(testCharacter.getRaceUuid()));
        assertTrue(backgroundRepo.existsById(testCharacter.getBackgroundUuid()));
    }

    @Test
    public void testObjectTypesCorrect() {
        // Test that fields have correct types
        assertNotNull(testCharacter.getAbilityScores());
        assertNotNull(testCharacter.getHpHandler());
        assertNotNull(testCharacter.getDeathSavingThrowsHelper());
        
        // Test that objects are of correct classes
        assertTrue(testCharacter.getAbilityScores() instanceof Map);
        assertTrue(testCharacter.getHpHandler() instanceof HPHandler);
        assertTrue(testCharacter.getDeathSavingThrowsHelper() instanceof DeathSavingThrowsHelper);
    }
}