import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CoinPurse } from './coin-purse-component';

describe('CoinPurse', () => {
  let component: CoinPurse;
  let fixture: ComponentFixture<CoinPurse>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CoinPurse]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CoinPurse);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
