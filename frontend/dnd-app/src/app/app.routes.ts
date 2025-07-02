import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login-component';
import { SelectCharacter } from './components/select-character/select-character';
import { CharacterInfo } from './components/character-info/character-info-component';
import { Inventory } from './components/inventory/inventory-component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'characters', component: SelectCharacter },
  { path: 'character/:charUuid', component: CharacterInfo },
  { path: 'character/:charUuid/inventory', component: Inventory },
  { path: '', redirectTo: '/login', pathMatch: 'full' }, 
  { path: '**', redirectTo: '/login' }
];