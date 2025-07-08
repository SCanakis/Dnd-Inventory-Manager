import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';

@Component({
  selector: 'app-nav-component',
  imports: [CommonModule],
  templateUrl: './nav-component.html',
  styleUrl: './nav-component.scss'
})
export class NavComponent implements OnInit{

  charUuid: string | null = null;
  currentPage : string = 'inventory'

  constructor(private router : Router, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.charUuid = this.route.snapshot.paramMap.get("charUuid");

    this.updateCurrentPage(this.router.url);

    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.updateCurrentPage(event.url);
      });

  }

  private updateCurrentPage(url : string) {
    if (url.includes('/inventory')) {
      this.currentPage = 'inventory';
    } else if (url.includes('/itemCatalog')) {
      this.currentPage = 'catalog';
    } else if (url.includes('/characterStats')) {
      this.currentPage = 'stats';
    } else {
      this.currentPage = 'inventory'; // Default fallback
    }
  }

  navigateToInventory() {
    if(this.charUuid) {
      this.currentPage = 'inventory'
      this.router.navigate(['/character', this.charUuid, 'inventory']);
    }
  }

  navigateToItemCatalog() {
    if(this.charUuid) {
      this.currentPage = 'catalog'
      this.router.navigate(['/character', this.charUuid, 'itemCatalog']);
    }
  }

  navigateToCharacterStats() {
    if(this.charUuid) {
      this.currentPage = 'stats'
      this.router.navigate(['/character', this.charUuid, 'characterStats']);
    }

  }

}
