import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { AbilityScore, CharacterBasicInfoView } from './character.types';

@Component({
  selector: 'app-character-info',
  standalone: true,
  imports: [CommonModule], 
  templateUrl: './character-info.html',
  styleUrls: ['./character-info.scss']
})
export class CharacterInfo implements OnInit {
	private http = inject(HttpClient)
	data : CharacterBasicInfoView | null = null;
	loading = false;
	error: string | null = null;

	ngOnInit() {
		this.loadData();
	}

	loadData() {
		this.http.get<CharacterBasicInfoView>("http://localhost:8080/character/6380e09d-f328-41e6-b822-548e008f6822")
			.subscribe({
				next: (response) => {
					this.data = response;
					console.log("Data loaded:" + response);
				},
				error : (error) => {
					console.log("API Error:", error);
				}
			});
	}

	getAbilityScore(ability: AbilityScore): number {
    return this.data?.abilityScores[ability] || 0;
  }

  getAbilityModifier(ability: AbilityScore): number {
    const score = this.getAbilityScore(ability);
    return Math.floor((score - 10) / 2);
  }

  getTotalLevel(): number {
    return this.data?.classes.reduce((total, cls) => total + cls.level, 0) || 0;
  }

  getAbilityScoreKeys(): AbilityScore[] {
  return Object.values(AbilityScore);
  }
  getAbilityScorePositive(ability: AbilityScore): string {
		let value = this.getAbilityScore(ability);
		if(value > 0) {
			return "+";
		} 
		if(value == 0) {
			return "";
		} else {
			return "-"
		}
  }
}
