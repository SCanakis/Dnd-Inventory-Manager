import { Component, OnInit } from '@angular/core';
import { BasicCharacterInfoComponent } from '../basic-character-info/basic-character-info-component';
import { NavComponent } from '../nav/nav-component';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { CharacterInfoService } from '../../../service/character-info/character-info-service';
import { AbilityScore, CharacterBasicInfoView } from '../../../interface/character-info-interface';
import { error } from 'console';
import { HttpErrorResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { WebSocketResponse } from '../../../interface/websocket-interface';
import { WebsocketServiceCharacterStats } from '../../../service/websocket/websocket-service-character-stats';
import { response } from 'express';

@Component({
  selector: 'app-character-stats',
  imports: [BasicCharacterInfoComponent, NavComponent, CommonModule],
  templateUrl: './character-stats.html',
  styleUrl: './character-stats.scss'
})
export class CharacterStats implements OnInit{

  private subscriptions : Subscription[] = [];

  isConnected = false;

  lastMessage : WebSocketResponse | null = null;


  charUuid : string | null;
  characterInfo : CharacterBasicInfoView | null = null;

  constructor(
    private characterWebSocketService : WebsocketServiceCharacterStats,
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

    this.characterWebSocketService.init(this.charUuid);
    this.characterWebSocketService.connect();

    this.subscriptions.push(
      this.characterWebSocketService.isConnected$.subscribe(connected => {
        this.isConnected = connected;
        if(connected && this.charUuid) {
          this.characterWebSocketService.subscribeToCharacterStats(this.charUuid);
        }
      })
    );

    this.subscriptions.push(
      this.characterWebSocketService.characterUpdates$.subscribe(response => {
        if(response) {
          this.lastMessage = response;

          if(response.data) {
            this.characterInfo = response.data;
          }

        } else {
          console.log("Character STat is null");
        }
      })
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
