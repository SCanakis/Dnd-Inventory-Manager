import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Background, BasicCharInfoCreationDTO, DndClass, Race, SubClass } from '../../../interface/character-creation-interface';
import { CharacterCreationService } from '../../../service/character-creation-service/character-creation-service';
import { AbilityScore, CharacterClassDetail } from '../../../interface/character-info-interface';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-character-creation',
  imports: [CommonModule, FormsModule],
  templateUrl: './character-creation.html',
  styleUrl: './character-creation.scss'
})
export class CharacterCreation implements OnInit {

  // Data arrays
  backgrounds: Background[] = [];
  races: Race[] = [];
  classes: DndClass[] = [];
  subClasses: SubClass[] = [];
  
  // Character creation DTO
  characterDTO: BasicCharInfoCreationDTO;
  
  // UI state
  isLoading = true;
  isSaving = false;
  errorMessage = '';
  
  // Form state
  selectedBackground: Background | null = null;
  selectedRace: Race | null = null;
  
  // Temporary selection state for class cards
  selectedSubClassForClass: { [classUuid: string]: string } = {};
  
  // Ability scores enum for template
  AbilityScore = AbilityScore;
  abilityScoreKeys = Object.values(AbilityScore);

  constructor(
    private creationService: CharacterCreationService,
    private router: Router
  ) {
    this.characterDTO = new BasicCharInfoCreationDTO();
  }

  ngOnInit(): void {
    this.loadInitialData();
  }

  loadInitialData(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    Promise.all([
      this.loadBackgrounds(),
      this.loadRaces(),
      this.loadClasses(),
      this.loadSubClasses()
    ]).then(() => {
      this.isLoading = false;
    }).catch((error) => {
      this.isLoading = false;
      this.errorMessage = 'Failed to load character creation data. Please try again.';
      console.error('Error loading initial data:', error);
    });
  }

  private loadBackgrounds(): Promise<Background[]> {
    return new Promise((resolve, reject) => {
      this.creationService.getAllBackgrounds().subscribe({
        next: (response: Background[]) => {
          this.backgrounds = response;
          resolve(response);
        },
        error: (error: HttpErrorResponse) => {
          console.error('Failed to load backgrounds:', error);
          reject(error);
        }
      });
    });
  }

  private loadRaces(): Promise<Race[]> {
    return new Promise((resolve, reject) => {
      this.creationService.getAllRaces().subscribe({
        next: (response: Race[]) => {
          this.races = response;
          resolve(response);
        },
        error: (error: HttpErrorResponse) => {
          console.error('Failed to load races:', error);
          reject(error);
        }
      });
    });
  }

  private loadClasses(): Promise<DndClass[]> {
    return new Promise((resolve, reject) => {
      this.creationService.getAllClasses().subscribe({
        next: (response: DndClass[]) => {
          this.classes = response;
          resolve(response);
        },
        error: (error: HttpErrorResponse) => {
          console.error('Failed to load classes:', error);
          reject(error);
        }
      });
    });
  }

  private loadSubClasses(): Promise<SubClass[]> {
    return new Promise((resolve, reject) => {
      this.creationService.getAllSubClasses().subscribe({
        next: (response: SubClass[]) => {
          this.subClasses = response;
          resolve(response);
        },
        error: (error: HttpErrorResponse) => {
          console.error('Failed to load subclasses:', error);
          reject(error);
        }
      });
    });
  }

  // ========================================
  // SELECTION METHODS
  // ========================================

  selectBackground(background: Background): void {
    this.selectedBackground = background;
    this.characterDTO.setBackgroundUuid(background.backgroundUuid);
  }

  selectRace(race: Race): void {
    this.selectedRace = race;
    this.characterDTO.setRaceUuid(race.raceUuid);
  }

  /**
   * Add a class (called when clicking the "Add Class" button on a class card)
   */
  addClass(dndClass: DndClass): void {
    const classDetails = this.characterDTO.getCharacterClassDetails();
    
    // Check if class already exists
    if (classDetails.find(cd => cd.classUuid === dndClass.classUuid)) {
      console.log('Class already exists');
      return;
    }

    // Get selected subclass (if any) - ensure proper null handling
    const selectedSubClassUuid = this.selectedSubClassForClass[dndClass.classUuid];
    const cleanSubClassUuid = selectedSubClassUuid && selectedSubClassUuid !== 'undefined' && selectedSubClassUuid !== '' ? selectedSubClassUuid : null;
    let subClassName = '';
    
    if (cleanSubClassUuid) {
      const subClass = this.subClasses.find(sc => sc.subclassUuid === cleanSubClassUuid);
      subClassName = subClass?.name || '';
    }

    const newClassDetail: CharacterClassDetail = {
      classUuid: dndClass.classUuid,
      className: dndClass.name,
      hitDiceValue: dndClass.hitDiceValue,
      subclassUuid: cleanSubClassUuid,
      subclassName: subClassName,
      level: 1,
      hitDiceRemaining: 1
    };
    
    classDetails.push(newClassDetail);
    this.characterDTO.setCharacterClassDetails(classDetails);
    
    // Clear the selection for this class
    delete this.selectedSubClassForClass[dndClass.classUuid];
  }

  /**
   * Remove a class
   */
  removeClass(index: number): void {
    const classDetails = this.characterDTO.getCharacterClassDetails();
    if (index >= 0 && index < classDetails.length) {
      classDetails.splice(index, 1);
      this.characterDTO.setCharacterClassDetails(classDetails);
    }
  }

  /**
   * Update class level
   */
  updateClassLevel(classIndex: number, level: number): void {
    const classDetails = this.characterDTO.getCharacterClassDetails();
    if (classIndex >= 0 && classIndex < classDetails.length && level >= 1 && level <= 20) {
      const totalOtherLevels = classDetails.reduce((sum, cd, i) => 
        i !== classIndex ? sum + cd.level : sum, 0
      );
      
      if (totalOtherLevels + level <= 20) {
        classDetails[classIndex].level = level;
        classDetails[classIndex].hitDiceRemaining = level;
        this.characterDTO.setCharacterClassDetails(classDetails);
      }
    }
  }

  /**
   * NEW: Handle subclass change for already added classes
   */
  onSelectedClassSubclassChange(classIndex: number, event: Event): void {
    const target = event.target as HTMLSelectElement;
    const subclassUuid = target.value;
    
    // Clean the subclass UUID - convert empty string or 'undefined' to null
    const cleanSubClassUuid = subclassUuid && subclassUuid !== 'undefined' && subclassUuid !== '' ? subclassUuid : null;
    
    const classDetails = this.characterDTO.getCharacterClassDetails();
    if (classIndex >= 0 && classIndex < classDetails.length) {
      const subClass = cleanSubClassUuid ? 
        this.subClasses.find(sc => sc.subclassUuid === cleanSubClassUuid) : 
        null;
      
      classDetails[classIndex].subclassUuid = cleanSubClassUuid;
      classDetails[classIndex].subclassName = subClass?.name || '';
      
      this.characterDTO.setCharacterClassDetails(classDetails);
    }
  }

  // ========================================
  // HELPER METHODS
  // ========================================

  getSubClassesForClass(classUuid: string): SubClass[] {
    return this.subClasses.filter(sc => sc.classSource === classUuid);
  }

  hasSubClasses(classUuid: string): boolean {
    return this.getSubClassesForClass(classUuid).length > 0;
  }

  isClassSelected(classUuid: string): boolean {
    return this.characterDTO.getCharacterClassDetails()
      .some(cd => cd.classUuid === classUuid);
  }

  canAddClass(dndClass: DndClass): boolean {
    // Can't add if already selected
    if (this.isClassSelected(dndClass.classUuid)) {
      return false;
    }
    
    // Can't add if at level 20
    if (this.getTotalCharacterLevel() >= 20) {
      return false;
    }
    
    // If class has subclasses, must select one (but allow empty selection)
    // Remove the subclass requirement for adding classes
    return true;
  }

  // ========================================
  // ABILITY SCORE METHODS
  // ========================================

  updateAbilityScore(ability: AbilityScore, value: number): void {
    try {
      this.characterDTO.setAbilityScore(ability, value);
    } catch (error) {
      console.error('Invalid ability score:', error);
    }
  }

  getAbilityScore(ability: AbilityScore): number {
    return this.characterDTO.getAbilityScore(ability);
  }

  getAbilityModifier(ability: AbilityScore): number {
    const score = this.getAbilityScore(ability);
    return Math.floor((score - 10) / 2);
  }

  // ========================================
  // UTILITY METHODS
  // ========================================

  getTotalCharacterLevel(): number {
    return this.characterDTO.getTotalLevel();
  }

  // ========================================
  // EVENT HANDLERS
  // ========================================

  onClassLevelChange(classIndex: number, event: Event): void {
    const target = event.target as HTMLInputElement;
    const level = parseInt(target.value, 10);
    if (!isNaN(level)) {
      this.updateClassLevel(classIndex, level);
    }
  }

  onAbilityScoreChange(ability: AbilityScore, event: Event): void {
    const target = event.target as HTMLInputElement;
    const score = parseInt(target.value, 10);
    if (!isNaN(score)) {
      this.updateAbilityScore(ability, score);
    }
  }

  onSubClassSelectionChange(classUuid: string, event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.selectedSubClassForClass[classUuid] = target.value;
  }

  // ========================================
  // CHARACTER CREATION
  // ========================================

  createCharacter(): void {
    if (!this.isFormValid()) {
      return;
    }
    
    this.isSaving = true;
    this.errorMessage = '';
    
    console.log('Creating character with classes:', this.characterDTO.getCharacterClassDetails());
    
    this.creationService.createCharacter(this.characterDTO).subscribe({
      next: (response) => {
        this.isSaving = false;
        this.router.navigate(['/characters']);
      },
      error: (error: HttpErrorResponse) => {
        this.isSaving = false;
        this.errorMessage = 'Failed to create character. Please try again.';
        console.error('Character creation failed:', error);
      }
    });
  }

  isFormValid(): boolean {
    return this.characterDTO.getName().trim() !== '' &&
           this.selectedBackground !== null &&
           this.selectedRace !== null &&
           this.characterDTO.getCharacterClassDetails().length > 0;
  }

  cancel(): void {
    this.router.navigate(['/characters']);
  }

  reset(): void {
    this.characterDTO = new BasicCharInfoCreationDTO();
    this.selectedBackground = null;
    this.selectedRace = null;
    this.selectedSubClassForClass = {};
  }
}