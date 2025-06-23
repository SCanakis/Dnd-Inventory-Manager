import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CharacterInfo } from './components/character-info/character-info';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CharacterInfo],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected title = 'dnd-app';
}
