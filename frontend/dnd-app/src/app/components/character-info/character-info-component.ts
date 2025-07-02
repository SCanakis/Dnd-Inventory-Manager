import { CommonModule } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { AbilityScore, CharacterBasicInfoView } from '../../../interface/character-info-interface';
import { CharacterInfoService } from '../../../service/character-info/character-info-service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-character-info',
  standalone: true,
  imports: [CommonModule], 
  templateUrl: './character-info.html',
  styleUrls: ['./character-info.scss']
})
export class CharacterInfo implements OnInit{
	public character : CharacterBasicInfoView | null = null;
	private charUuid : string | null;

	constructor(private characterService: CharacterInfoService, private router : ActivatedRoute){
		this.charUuid = this.router.snapshot.paramMap.get('charUuid');
	}

	ngOnInit(): void {
		this.loadCharacters();
	}

	public loadCharacters() : void {
		if(!this.charUuid) {
			console.error("Character Uuid no found in route parameter");
			return;
		}

		this.characterService.getCharacter(this.charUuid).subscribe(
			(response : CharacterBasicInfoView) => {
				this.character = response;
			}, 
			(error :HttpErrorResponse) => {
				console.log("Error loading character:", error.message)
			}
		);
	}

	public getAbilityScoreKeys() : AbilityScore[] {
		return Object.values(AbilityScore);
	}

	public getAbilityScore(ability: AbilityScore) : number {
		return this.character?.abilityScores[ability] || 0;
	}

	public getAbilityModifier(ability: AbilityScore) : number {
		let score = this.getAbilityScore(ability);	
		return Math.floor((score-10)/2);
	}

	public getAbilityScorePositive(ability : AbilityScore) : string {
		let score = this.getAbilityScore(ability);

		if(score > 11) {
			return "+";
		} 
		return " ";
	}

	public getTotalLevel() : number {
		if(!this.character?.classes) {
			return 0;
		}
		return this.character.classes.reduce((total, cls) => total + (cls.level || 0), 0);
	}
	
	

}

