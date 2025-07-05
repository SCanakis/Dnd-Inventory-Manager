import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ItemCatalogInterface } from '../../interface/item-catalog-interface';
import { environment } from '../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class ItemCatalogHttp {

  private apiUrl = environment.apiUrl;

  constructor(private http : HttpClient) { }

  getItem(itemUuid : string) : Observable<ItemCatalogInterface> {
    return this.http.get<ItemCatalogInterface>(`${this.apiUrl}/itemCatalog/id=${itemUuid}`, {
      withCredentials: true
    });
  }
  addItem(itemUuid : string, charUuid : string, quantity : number) : Observable<Boolean> {
    const params = new HttpParams().set('quantity', quantity);
    
    return this.http.post<Boolean>(
        `${this.apiUrl}/itemCatalog/id=${itemUuid}/charId=${charUuid}`,
        null, 
        {
            withCredentials: true,
            params: params
        }
    );
  } 

}
