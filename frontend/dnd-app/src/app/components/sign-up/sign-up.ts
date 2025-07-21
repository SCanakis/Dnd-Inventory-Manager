import { Component } from '@angular/core';
import { AuthService } from '../../../service/auth/auth-service';
import { response } from 'express';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { error } from 'console';

@Component({
  selector: 'app-sign-up',
  imports: [CommonModule, FormsModule],
  templateUrl: './sign-up.html',
  styleUrl: './sign-up.scss'
})
export class SignUp {

  username = '';
  password = '';
  passwordConfirmation = '';

  errorMessage = '';
  isLoading = false;


  constructor(
    private authService : AuthService,
    private router : Router
  ) {}

  onSubmit() : void {
    if(!this.username || !this.password || !this.passwordConfirmation) {
      this.errorMessage = 'Plase enter both username and password';
      return;
    }

    if(this.password != this.passwordConfirmation) {
      this.errorMessage = 'Passwords are not same';
    }

    this.isLoading = true;
    this.errorMessage = '';


    this.authService.createUser(this.username, this.password).subscribe({
      next: (response) => {
        console.log("Sign Up succesfully", response);
        this.router.navigate(["/login"]);
      },
      error: (error) => {
        console.log('Sign Up failed', error);
        this.errorMessage = "Something went wrong";
        this.isLoading = false;
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }

  goToLogin() {
    this.router.navigate(["/login"])
  }


}
