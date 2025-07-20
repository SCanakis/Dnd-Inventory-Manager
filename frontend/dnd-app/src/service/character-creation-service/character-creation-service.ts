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
    return this.http.get<Background[]>(`${this.apiUrl}/background`, 
      {withCredentials: true}
    );
  }

  getAllRaces() : Observable<Race[]> {
    return this.http.get<Race[]>(`${this.apiUrl}/race`,
      {withCredentials: true}
    );
  }

  getAllClasses() : Observable<DndClass[]> {
    return this.http.get<DndClass[]>(`${this.apiUrl}/classes`, {
      withCredentials: true
    });
  }

  getAllSubClasses() : Observable<SubClass[]> {
    return this.http.get<SubClass[]>(`${this.apiUrl}/subclasses`, {
      withCredentials : true
    });
  }

  getSubClassesForSourceClass(classUuid : string) : Observable<SubClass[]> {
    return this.http.get<SubClass[]>(`${this.apiUrl}/subclasses/id=${classUuid}`, {
      withCredentials : true
    });
  }


  createCharacter(charInfoDTO : BasicCharInfoCreationDTO) {
    
    const serializable = charInfoDTO.toSerializableObject();
    return this.http.post<CharacterBasicInfoView>(
      `${this.apiUrl}/character`,
      serializable, {
      withCredentials : true,
    });
  }


  
}
