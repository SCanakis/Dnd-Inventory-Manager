import { CommonModule } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { response } from 'express';
import { AbilityScore, CharacterBasicInfoView } from '../../../interface/character-info';
import { CharacterInfoService } from '../../../service/character-info';

@Component({
  selector: 'app-character-info',
  standalone: true,
  imports: [CommonModule], 
  templateUrl: './character-info.html',
  styleUrls: ['./character-info.scss']
})
export class CharacterInfo implements OnInit{
	public character : CharacterBasicInfoView | null = null;
	
	private charUuid : string = "6380e09d-f328-41e6-b822-548e008f6822";
	constructor(private characterService: CharacterInfoService){}

	ngOnInit(): void {
		this.getCharacter();
	}

	public getCharacter() : void {
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
		return Math.floor((score-10/2));
	}

	public getAbilityScorePositive(ability : AbilityScore) : string {
		let score = this.getAbilityScore(ability);

		if(!score || score == 10) {
			return " ";
		} else if(score > 10) {
			return "+";
		} else {
			return "-";
		}
	}

	public getTotalLevel() : number {
		if(!this.character?.classes) {
			return 0;
		}
		return this.character.classes.reduce((total, cls) => total + (cls.level || 0), 0);
	}
	
	

}

