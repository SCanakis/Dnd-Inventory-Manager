import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-nav-component',
  imports: [CommonModule],
  templateUrl: './nav-component.html',
  styleUrl: './nav-component.scss'
})
export class NavComponent implements OnInit{

  charUuid: string | null = null;

  constructor(private router : Router, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.charUuid = this.route.snapshot.paramMap.get("charUuid");
  }

  navigateToInventory() {
    if(this.charUuid) {
      this.router.navigate(['/character', this.charUuid, 'inventory']);
    }
  }

}
