import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItemCatalog } from './item-catalog';

describe('ItemCatalog', () => {
  let component: ItemCatalog;
  let fixture: ComponentFixture<ItemCatalog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ItemCatalog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ItemCatalog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
