import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import { HttpClient, HttpParams } from '@angular/common/http';
import { max, Observable } from 'rxjs';
import { Container, ContainerView } from '../../interface/container-interface';

@Injectable({
  providedIn: 'root'
})
export class ContainerService {
  private apiUrl = environment.apiUrl;

  constructor(private http : HttpClient) {}

  getContainers(charUuid : string) : Observable<ContainerView[]> {
    return this.http.get<ContainerView[]>(`${this.apiUrl}/containers/${charUuid}`, {
      withCredentials: true
    });
  }

  createContainer(charUuid: string, container : Container): Observable<Container> {
    return this.http.post<Container>(`${this.apiUrl}/containers/${charUuid}`, {
      withCredentials: true
    }); 
  }

  deleteContainer(charUuid: string, containerUuid : string): Observable<Boolean> {
    return this.http.delete<Boolean>(`${this.apiUrl}/containers/${charUuid}/conatinerId=${containerUuid}`, {
      withCredentials: true
    }); 
  }

  updateMaxCapacityOfContainer(charUuid: string, containerUuid : string, maxCapactiy : number): Observable<Container> {
    const params = new HttpParams().set('maxCapacity', maxCapactiy.toString())
    return this.http.put<Container>(`${this.apiUrl}/containers/${charUuid}/conatinerId=${containerUuid}`, {
      params : params,
      withCredentials: true
    }); 
  }
}
