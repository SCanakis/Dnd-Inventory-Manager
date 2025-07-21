import { Component, OnDestroy, OnInit } from '@angular/core';
import { BasicCharacterInfoComponent } from '../basic-character-info/basic-character-info-component';
import { NavComponent } from '../nav/nav-component';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { AbilityScore, CharacterBasicInfoView, CharacterInfoUpdateDTO} from '../../../interface/character-info-interface';
import { HttpErrorResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { WebSocketResponse } from '../../../interface/websocket-interface';
import { WebsocketServiceCharacterStats } from '../../../service/websocket/character-stats/websocket-service-character-stats';
import { ContainerService } from '../../../service/container/container';
import { ContainerView } from '../../../interface/container-interface';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-character-stats',
  imports: [BasicCharacterInfoComponent, NavComponent, CommonModule, FormsModule],
  templateUrl: './character-stats.html',
  styleUrl: './character-stats.scss'
})
export class CharacterStats implements OnInit, OnDestroy{

  private subscriptions : Subscription[] = [];

  isConnected = false;
  showUpdateModal = false;

  lastMessage : WebSocketResponse | null = null;

  charUuid : string | null;
  characterInfo : CharacterBasicInfoView | null = null;

  inventoryContainers : ContainerView | null = null;

  updateData : CharacterInfoUpdateDTO;


  constructor(
    private characterWebSocketService : WebsocketServiceCharacterStats,
    private route : ActivatedRoute,
    private containerServce : ContainerService

  ) {
    this.charUuid = route.snapshot.paramMap.get('charUuid');
    this.updateData = new CharacterInfoUpdateDTO();
  }

  ngOnInit(): void {
      this.loadCharacterStats();
      this.loadInventoryContainer();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }



  public loadInventoryContainer() : void {
    if(this.charUuid) {
      this.containerServce.getContainers(this.charUuid).subscribe(
        (response : ContainerView[]) => {
          for(let current of response) {
            if(current.container.id.containerUuid === '00000000-0000-0000-0000-000000000000') {
              this.inventoryContainers = current;
              break;
            }
          }
        },
        (error : HttpErrorResponse) => {
          console.log("Error loading Inventory: ", error);
        }
      );
    } else {
      console.log("charUuid was not initalized");
    }
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

    this.subscribeToCharacterBroadcasts();
  }

  private populateUpdateData(): void {
  if (this.characterInfo) {
    this.updateData.name = this.characterInfo.name;
    this.updateData.raceUuid = this.characterInfo.raceUUID;
    this.updateData.backgroundUuid = this.characterInfo.backgroundUUID;
    this.updateData.inspiration = this.characterInfo.inspiration;
    
    this.updateData.abilityScores = { ...this.characterInfo.abilityScores };

    this.updateData.hpHandler = { ...this.characterInfo.hpHandler };
    this.updateData.deathSavingThrowsHelper = { ...this.characterInfo.deathSavingThrowsHelper };
    this.updateData.characterClassDetail = [...this.characterInfo.classes];
  }
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

  public updateCharacteName(name : string) : void {
    this.updateData.setName(name);
  }

  public updateInspiration(inspiration : boolean) : void {
    this.updateData.setInspiration(inspiration);
  }

  public updateAbilityScore(ability: AbilityScore, value: number): void {
  console.log(`updateAbilityScore called: ability=${ability}, value=${value}`);
  
  if (value < 1 || value > 30) {
    console.log(`Value ${value} is out of range (1-30), returning`);
    return;
  }

  if (this.inventoryContainers && ability === AbilityScore.strength) {
    let currentCapacity = this.inventoryContainers.container.currentCapacity;
    console.log(`Checking strength capacity: currentCapacity=${currentCapacity}, newStrength*15=${value * 15}`);
    if (currentCapacity > value * 15) {
      console.log(`Capacity check failed, returning`);
      return;
    }
  }

  console.log(`Setting ${ability} = ${value}`);
  this.updateData.setAbilityScore(ability, value);
  
  console.log(`Updated abilityScores:`, this.updateData.abilityScores);
}

  public openUpdateModal(): void {
    this.populateUpdateData();
    this.showUpdateModal = true;
  }
  
  public closeUpdateModal() : void {
    this.showUpdateModal = false;
  }

  public isValidUpdate(): boolean {
    if (!this.updateData.name || this.updateData.name.trim() === '') {
      return false;
    }

    if (this.updateData.hpHandler) {
      if (this.updateData.hpHandler.currentHp < 0 || 
          this.updateData.hpHandler.maxHp < 1) {
        return false;
      }
    }

    if (this.updateData.deathSavingThrowsHelper) {
      if (this.updateData.deathSavingThrowsHelper.successes < 0 || 
          this.updateData.deathSavingThrowsHelper.successes > 3 ||
          this.updateData.deathSavingThrowsHelper.failures < 0 || 
          this.updateData.deathSavingThrowsHelper.failures > 3) {
        return false;
      }
    }

    if (this.updateData.abilityScores) {
      for (const score of Object.values(this.updateData.abilityScores)) {
        if (score < 1 || score > 30) {
          return false;
        }
      }
    }
    return true;
  }


  public onHpChange(type: 'current' | 'temp', value: number): void {
    if (!this.characterInfo) return;

    // Validate the value
    if (type === 'current') {
      value = Math.max(0, Math.min(value, this.characterInfo.hpHandler.maxHp));
      this.characterInfo.hpHandler.currentHp = value;
    } else if (type === 'temp') {
      value = Math.max(0, value);
      this.characterInfo.hpHandler.temporaryHp = value;
    }

    this.sendQuickUpdate('hp', { type, value });
  }

  public onDeathSaveChange(type: 'successes' | 'failures', value: number): void {
    if (!this.characterInfo) return;

    // Validate the value (0-3)
    value = Math.max(0, Math.min(3, value));
    
    if (type === 'successes') {
      this.characterInfo.deathSavingThrowsHelper.successes = value;
    } else {
      this.characterInfo.deathSavingThrowsHelper.failures = value;
    }

    this.sendQuickUpdate('deathSaves', { type, value });
  }

  public onInspirationChange(value: boolean): void {
    if (!this.characterInfo) return;

    this.characterInfo.inspiration = value;

    this.sendQuickUpdate('inspiration', { value });
  }

  public onHitDiceChange(classIndex: number, value: number): void {
    if (!this.characterInfo || !this.characterInfo.classes[classIndex]) return;

    const characterClass = this.characterInfo.classes[classIndex];
    
    value = Math.max(0, Math.min(value, characterClass.level));
    
    characterClass.hitDiceRemaining = value;

    this.sendQuickUpdate('hitDice', { 
      classIndex, 
      classUuid: characterClass.classUuid, 
      value 
    });
  }


  sendQuickUpdate(updateType : string, data : any) {
    if(!this.charUuid || !this.characterInfo) {
      console.log("Cannot send update - mssing character UUID of info");
      return;
    }

    const quickUpdateData = new CharacterInfoUpdateDTO();

    switch(updateType) {
      case 'hp':
        quickUpdateData.hpHandler = {...this.characterInfo.hpHandler};
        console.log("HP Update");
        break;
      case 'deathSaves':
        quickUpdateData.deathSavingThrowsHelper= {...this.characterInfo.deathSavingThrowsHelper};
        console.log("Death Saves Update");
        break;
      case 'inspiration':
        quickUpdateData.inspiration = this.characterInfo.inspiration;
        console.log("Update Inspritation");
        break;
      case 'hitDice':
        quickUpdateData.characterClassDetail = [...this.characterInfo.classes];
        console.log("Update Hit Dice");
        break;
      default:
        console.warn("Unkown update type")
        return;
    }
    this.characterWebSocketService.updateCharacterInfo(this.charUuid, quickUpdateData);
  }

  
  saveCharacterUpdates() {
    if(!this.charUuid) {
      return;
    }

    console.log('Sending update data:', this.updateData);
    this.characterWebSocketService.updateCharacterInfo(this.charUuid, this.updateData);
    this.closeUpdateModal();
  }


  private subscribeToCharacterBroadcasts() : void {
    this.subscriptions.push(
      this.characterWebSocketService.characterBroadcasts$.subscribe(
        response => {
          if(response && response.data) {
            this.characterInfo = response.data;
          }
        })
    );
  }  

}
