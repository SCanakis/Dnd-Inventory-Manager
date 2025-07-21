import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login-component';
import { SelectCharacter } from './components/select-character/select-character';
import { Inventory } from './components/inventory/inventory-component';
import { ItemCatalog } from './components/item-catalog/item-catalog';
import { CharacterStats } from './components/character-stats/character-stats';
import { CharacterCreation } from './components/character-creation/character-creation';
import { SignUp } from './components/sign-up/sign-up';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'characters', component: SelectCharacter },
  { path: 'character/:charUuid/inventory', component: Inventory },
  { path: 'character/:charUuid/itemCatalog', component: ItemCatalog },
  { path : 'character/:charUuid/characterStats', component: CharacterStats},
  { path : 'character-creation', component: CharacterCreation},
  { path : 'sign-up', component: SignUp},
  { path: '', redirectTo: '/login', pathMatch: 'full' }, 
  { path: '**', redirectTo: '/login' }
];