import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CharacterInfo } from './components/character-info/character-info-component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CharacterInfo, CommonModule, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected title = 'dnd-app';
}
