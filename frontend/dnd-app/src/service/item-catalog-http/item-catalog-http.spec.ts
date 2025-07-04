import { TestBed } from '@angular/core/testing';

import { ItemCatalogHttp } from './item-catalog-http';

describe('ItemCatalogHttp', () => {
  let service: ItemCatalogHttp;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ItemCatalogHttp);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
