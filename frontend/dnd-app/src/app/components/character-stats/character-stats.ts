import { Component, OnInit } from '@angular/core';
import { BasicCharacterInfoComponent } from '../basic-character-info/basic-character-info-component';
import { NavComponent } from '../nav/nav-component';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { CharacterInfoService } from '../../../service/character-info/character-info-service';
import { AbilityScore, CharacterBasicInfoView } from '../../../interface/character-info-interface';
import { error } from 'console';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-character-stats',
  imports: [BasicCharacterInfoComponent, NavComponent, CommonModule],
  templateUrl: './character-stats.html',
  styleUrl: './character-stats.scss'
})
export class CharacterStats implements OnInit{

  charUuid : string | null;
  characterInfo : CharacterBasicInfoView | null = null;

  constructor(
    private characterService : CharacterInfoService,
    private route : ActivatedRoute
  ) {
    this.charUuid = route.snapshot.paramMap.get('charUuid');
  }

  ngOnInit(): void {
      this.loadCharacterStats();
  }

  public loadCharacterStats() : void {
    if(!this.charUuid) {
      console.error("Cahracter Uuid not found in router paramater");
      return;
    }

    this.characterService.getCharacter(this.charUuid).subscribe(
      (response : CharacterBasicInfoView) => {
        this.characterInfo = response;
      },
      (error : HttpErrorResponse) => {
        console.log("Error loading character: ", error.message);
      }
    );
  }

  public getAbilityScoreKeys() : AbilityScore[] {
    return Object.values(AbilityScore);
  }

  public getAbilityScore(ability : AbilityScore) : number {
    return this.characterInfo?.abilityScores[ability] || 0;
  }


  getAbilityScoreModifier(ability : AbilityScore) : number {
    let score = this.getAbilityScore(ability);
    return Math.floor((score-10)/2);
  }


  public getAbilityScorePositive(ability : AbilityScore) : string{
    let score = this.getAbilityScore(ability);

    if(score > 11) {
      return "+";
    }
    return " ";
  }




}
