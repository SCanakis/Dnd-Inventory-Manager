import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable, tap } from 'rxjs';
import { environment } from '../environments/environment.development';
import { error } from 'console';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http : HttpClient) {}

  login(username : string, password : string): Observable<any> {
    const formData = new FormData();
    formData.append('username', username);
    formData.append('password', password);

    return this.http.post(`${this.apiUrl}/api/auth/login`, formData, {
      withCredentials: true
    });
  }

  logout() : Observable<any> {
    return this.http.post(`${this.apiUrl}/api/auth/logout`, {}, {
      withCredentials : true
    }).pipe(
      tap(response => {
        console.log('Login response:', response); // Add this for debugging
      }),
      catchError(error => {
        console.error('Login error details:', error); // Add this for debugging
        throw error;
      })
    );
  }

  getCharacters() : Observable<any> {
    return this.http.get(`${this.apiUrl}/characters/`, {
      withCredentials: true
    });
  }

  checkAuthStatus(): void {
    this.getCharacters().subscribe(
      () => this.isAuthenticatedSubject.next(true),
      () => this.isAuthenticatedSubject.next(false)
    );
  }

  isLoggedIn(): boolean {
    return this.isAuthenticatedSubject.value;
  }

}
