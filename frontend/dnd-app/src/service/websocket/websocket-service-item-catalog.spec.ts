import { TestBed } from '@angular/core/testing';

import { WebsocketServiceItemCatalog } from './websocket-service-item-catalog';

describe('WebsocketServiceItemCatalog', () => {
  let service: WebsocketServiceItemCatalog;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WebsocketServiceItemCatalog);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
