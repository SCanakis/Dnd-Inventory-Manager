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
  availableClasses: DndClass[] = [];
  currentClassIndex = 0;
  
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
      this.loadClasses()
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
          this.availableClasses = response;
          resolve(response);
        },
        error: (error: HttpErrorResponse) => {
          console.error('Failed to load classes:', error);
          reject(error);
        }
      });
    });
  }

  // Selection methods
  selectBackground(background: Background): void {
    this.selectedBackground = background;
    this.characterDTO.setBackgroundUuid(background.backgroundUuid);
  }

  selectRace(race: Race): void {
    this.selectedRace = race;
    this.characterDTO.setRaceUuid(race.raceUuid);
  }

  selectClass(dndClass: DndClass): void {
    const classDetails = this.characterDTO.getCharacterClassDetails();
    const existingClassIndex = classDetails.findIndex(cd => cd.classUuid === dndClass.classUuid);
    
    if (existingClassIndex >= 0) {
      this.currentClassIndex = existingClassIndex;
    } else {
      const newClassDetail: CharacterClassDetail = {
        classUuid: dndClass.classUuid,
        className: dndClass.name,
        hitDiceValue: dndClass.hitDiceValue,
        subClassUuid: null,
        subClassName: '',
        level: 1,
        hitDiceRemaining: 1
      };
      
      classDetails.push(newClassDetail);
      this.characterDTO.setCharacterClassDetails(classDetails);
      this.currentClassIndex = classDetails.length - 1;
    }
    
    this.updateAvailableClasses();
  }

  removeClass(index: number): void {
    const classDetails = this.characterDTO.getCharacterClassDetails();
    if (index >= 0 && index < classDetails.length) {
      classDetails.splice(index, 1);
      this.characterDTO.setCharacterClassDetails(classDetails);
      
      if (this.currentClassIndex >= classDetails.length) {
        this.currentClassIndex = Math.max(0, classDetails.length - 1);
      }
      
      this.updateAvailableClasses();
    }
  }

  updateClassLevel(classIndex: number, level: number): void {
    const classDetails = this.characterDTO.getCharacterClassDetails();
    if (classIndex >= 0 && classIndex < classDetails.length && level >= 1 && level <= 20) {
      classDetails[classIndex].level = level;
      classDetails[classIndex].hitDiceRemaining = level;
      this.characterDTO.setCharacterClassDetails(classDetails);
    }
  }

  private updateAvailableClasses(): void {
    const selectedClassUuids = this.characterDTO.getCharacterClassDetails().map(cd => cd.classUuid);
    this.availableClasses = this.classes.filter(c => !selectedClassUuids.includes(c.classUuid));
  }

  // Ability score methods
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

  // Utility methods
  getSelectedClasses(): DndClass[] {
    const classDetails = this.characterDTO.getCharacterClassDetails();
    return classDetails.map(cd => 
      this.classes.find(c => c.classUuid === cd.classUuid)
    ).filter(c => c !== undefined) as DndClass[];
  }

  getClassLevel(classUuid: string): number {
    const classDetail = this.characterDTO.getCharacterClassDetails()
      .find(cd => cd.classUuid === classUuid);
    return classDetail?.level || 1;
  }

  getTotalCharacterLevel(): number {
    return this.characterDTO.getTotalLevel();
  }

  canAddMoreClasses(): boolean {
    return this.getTotalCharacterLevel() < 20 && 
           this.characterDTO.getCharacterClassDetails().length < this.classes.length;
  }

  // Event handlers for inputs
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

  // Character creation
  createCharacter(): void {
    if (!this.isFormValid()) {
      return;
    }
    
    this.isSaving = true;
    this.errorMessage = '';
    
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
    this.currentClassIndex = 0;
    this.updateAvailableClasses();
  }
}