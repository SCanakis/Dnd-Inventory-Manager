import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CharacterInfo } from './character-info-component';

describe('CharacterInfo', () => {
  let component: CharacterInfo;
  let fixture: ComponentFixture<CharacterInfo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CharacterInfo]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CharacterInfo);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
