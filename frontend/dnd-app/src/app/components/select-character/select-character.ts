import { Component, OnInit } from '@angular/core';
import { CharacterBasicInfoView } from '../../../interface/character-info-interface';
import { AuthService } from '../../../service/auth/auth-service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-select-character',
  imports: [CommonModule],
  templateUrl: './select-character.html',
  styleUrl: './select-character.scss'
})
export class SelectCharacter implements OnInit{
  characters: CharacterBasicInfoView[] | null = null;
  isLoading = true;
  errorMessage = '';
  
  constructor(private authService : AuthService, private router : Router) {}
  
  ngOnInit(): void {
      this.loadCharacter();
  }

  loadCharacter(): void {
    this.authService.getCharacters().subscribe({
      next: (characters)  => {
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
    })
  }

  getTotalLevel(character :CharacterBasicInfoView) : number {
		if(!character) {
			return 0;
		}
		return character.classes.reduce((total, cls) => total + (cls.level || 0), 0);
	}
  handleClick(charUuid : string) : void {
    this.router.navigate(['/character', charUuid, 'inventory']);
  }

  handleCreateCharacter() : void {
    this.router.navigate(['character-creation']);
  }
	

}
