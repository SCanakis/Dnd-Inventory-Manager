import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Background, BasicCharInfoCreationDTO, DndClass, Race, SubClass } from '../../interface/character-creation-interface';
import { CharacterBasicInfoView } from '../../interface/character-info-interface';


@Injectable({
  providedIn: 'root'
})
export class CharacterCreationService {

  private apiUrl = environment.apiUrl;

  constructor(private http : HttpClient) {}

  getAllBackgrounds() : Observable<Background[]> {
    return this.http.get<Background[]>(`${this.apiUrl}/background/`);
  }

  getAllRaces() : Observable<Race[]> {
    return this.http.get<Race[]>(`${this.apiUrl}/race/`);
  }

  getAllClasses() : Observable<DndClass[]> {
    return this.http.get<DndClass[]>(`${this.apiUrl}/classes/`);
  }

  getAllSubClasses() : Observable<SubClass[]> {
    return this.http.get<SubClass[]>(`${this.apiUrl}/subclasses/`);
  }

  getSubClassesForSourceClass(classUuid : string) : Observable<SubClass[]> {
    return this.http.get<SubClass[]>(`${this.apiUrl}/subclasses/id=${classUuid}`);
  }


  createCharacter(charInfoDTO : BasicCharInfoCreationDTO) {
    return this.http.post<CharacterBasicInfoView>(`${this.apiUrl}`, {
      body: charInfoDTO
    });
  }


  
}
