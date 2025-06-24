import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CharacterBasicInfoView } from '../interface/character-info';
import { environment } from '../environments/environment.development';


@Injectable({
  providedIn: 'root'
})
export class CharacterInfoService {
  private apiUrl = environment.apiUrl;

  constructor(private http : HttpClient) { }

  getCharacter(charUuid : String): Observable<CharacterBasicInfoView> {
    return this.http.get<CharacterBasicInfoView>(`${this.apiUrl}/character/${charUuid}`);
  }

  updateCahracter(charUuid : String, patch : CharacterBasicInfoView): Observable<CharacterBasicInfoView> {
    return this.http.put<CharacterBasicInfoView>(`${this.apiUrl}/character/${charUuid}`, patch);
  }
  deleteCharacter(charUuid : String) : Observable<CharacterBasicInfoView> {
    return this.http.delete<CharacterBasicInfoView>(`${this.apiUrl}/character/${charUuid}`);
  }
}
