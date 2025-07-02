import { Component, OnInit } from '@angular/core';
import { CharacterBasicInfoView } from '../../../interface/character-info-interface';
import { CharacterInfoService } from '../../../service/character-info/character-info-service';
import { ActivatedRoute } from '@angular/router';
import { error } from 'console';
import { HttpErrorResponse } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-basic-character-info-component',
  imports: [CommonModule],
  templateUrl: './basic-character-info-component.html',
  styleUrl: './basic-character-info-component.scss'
})
export class BasicCharacterInfoComponent implements OnInit{
  public character : CharacterBasicInfoView | null = null;
  private charUuid : string | null;

  constructor(private characterSerivce : CharacterInfoService, private router : ActivatedRoute) {
    this.charUuid = this.router.snapshot.paramMap.get('charUuid');
  }

  ngOnInit(): void {
    this.loadCharacter();
  }

  public loadCharacter() : void {
    if(!this.charUuid) {
      console.error("Character Uuid no found in route parameter");
			return;
    }

    this.characterSerivce.getCharacter(this.charUuid).subscribe(
        (response : CharacterBasicInfoView) => {
          this.character = response;
        },
        (error : HttpErrorResponse) => {
          console.log("Error loading character:", error.message)
        }
    )

  }
}
