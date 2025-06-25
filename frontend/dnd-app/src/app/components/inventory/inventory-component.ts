import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';

@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './inventory.html',
  styleUrl: './inventory.scss'
})

export class Inventory implements OnInit{

  private http = inject(HttpClient);
  
  ngOnInit(): void {
      
  }
}
