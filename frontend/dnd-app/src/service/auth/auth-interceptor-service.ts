import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptor implements HttpInterceptor{



  constructor(private router : Router) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
  
    const authReq = req.clone({
      setHeaders: {
        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
      }
    });

    return next.handle(authReq).pipe(
      catchError(error =>{
        if(error.status === 401) {
          this.router.navigate(['\login']);
        }
        return throwError(error);
      })
    )

  }
}
