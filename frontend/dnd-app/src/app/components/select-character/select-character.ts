import { Component, OnInit } from '@angular/core';
import { CharacterBasicInfoView } from '../../../interface/character-info-interface';
import { AuthService } from '../../../service/auth/auth-service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CharacterInfoService } from '../../../service/character-info/character-info-service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-select-character',
  imports: [CommonModule],
  templateUrl: './select-character.html',
  styleUrl: './select-character.scss'
})
export class SelectCharacter implements OnInit {
  characters: CharacterBasicInfoView[] | null = null;
  isLoading = true;
  errorMessage = '';
  
  // NEW: Properties for options and delete confirmation
  showOptionsModal = false;
  selectedCharacter: CharacterBasicInfoView | null = null;
  showDeleteConfirmation = false;
  characterToDelete: CharacterBasicInfoView | null = null;
  isDeleting = false;
  showDeleteUserConfirmation = false;
  isDeletingUser = false;
  
  constructor(
    private authService: AuthService,
    private router: Router,
    private charInfoService: CharacterInfoService
  ) {}
  
  ngOnInit(): void {
    this.loadCharacter();
  }

  ngOnDestroy(): void {
    // No need for document click listener anymore
  }

  loadCharacter(): void {
    this.authService.getCharacters().subscribe({
      next: (characters) => {
        this.characters = characters;
        this.isLoading = false;
      }, 
      error: (error) => {
        console.error('Failed to load characters', error);
        this.errorMessage = 'Failed to load Characters';
        this.isLoading = false;
        if(error.status === 401) {
          this.router.navigate(['/login']);
        }
      }
    });
  }
  
  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: (error) => {
        console.error('Failed to logout', error);
        this.router.navigate(['/login']);
      }
    });
  }

  getTotalLevel(character: CharacterBasicInfoView): number {
    if(!character) {
      return 0;
    }
    return character.classes.reduce((total, cls) => total + (cls.level || 0), 0);
  }

  handleClick(charUuid: string): void {
    this.router.navigate(['/character', charUuid, 'inventory']);
  }

  handleCreateCharacter(): void {
    this.router.navigate(['character-creation']);
  }

  // NEW: Options modal methods
  openOptionsModal(event: Event, character: CharacterBasicInfoView): void {
    event.stopPropagation();
    this.selectedCharacter = character;
    this.showOptionsModal = true;
  }

  closeOptionsModal(): void {
    this.showOptionsModal = false;
    this.selectedCharacter = null;
  }

  // NEW: Delete confirmation methods
  openDeleteConfirmation(event: Event, character: CharacterBasicInfoView): void {
    event.stopPropagation();
    this.characterToDelete = character;
    this.showDeleteConfirmation = true;
    this.closeOptionsModal(); // Close options modal when opening delete confirmation
  }

  closeDeleteConfirmation(): void {
    this.showDeleteConfirmation = false;
    this.characterToDelete = null;
    this.isDeleting = false;
  }

  confirmDelete(): void {
    if (!this.characterToDelete) return;
    
    this.isDeleting = true;
    this.charInfoService.deleteCharacter(this.characterToDelete.charInfoUUID).subscribe({
      next: () => {
        // Remove character from the list
        if (this.characters) {
          this.characters = this.characters.filter(
            char => char.charInfoUUID !== this.characterToDelete!.charInfoUUID
          );
        }
        this.closeDeleteConfirmation();
      },
      error: (error) => {
        console.error('Failed to delete character', error);
        this.errorMessage = 'Failed to delete character. Please try again.';
        this.isDeleting = false;
        // Don't close the confirmation dialog so user can retry
      }
    });
  }

  openDeleteUserConfirmation(): void {
    this.showDeleteUserConfirmation = true;
  }

  closeDeleteUserConfirmation(): void {
    this.showDeleteUserConfirmation = false;
    this.isDeletingUser = false;
  }

  confirmDeleteUser(): void {
    this.isDeletingUser = true;
    this.deleteUser();
  }

  // Update your existing deleteUser method to handle the modal:
  deleteUser(): void {
    this.authService.deleteUser().subscribe({
      next: (response: boolean) => {
        if (response === true) {
          this.router.navigate(['/login']);
        } else {
          console.log("Something went wrong deleting the account");
          this.errorMessage = "Failed to delete account. Please try again.";
          this.isDeletingUser = false;
        }
      },
      error: (error: HttpErrorResponse) => {
        console.log("Error deleting account: ", error);
        this.errorMessage = "Failed to delete account. Please try again.";
        this.isDeletingUser = false;
      }
    });
  }
}