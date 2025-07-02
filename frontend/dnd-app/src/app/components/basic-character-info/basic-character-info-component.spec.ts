import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BasicCharacterInfoComponent } from './basic-character-info-component';

describe('BasicCharacterInfoComponent', () => {
  let component: BasicCharacterInfoComponent;
  let fixture: ComponentFixture<BasicCharacterInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BasicCharacterInfoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BasicCharacterInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
