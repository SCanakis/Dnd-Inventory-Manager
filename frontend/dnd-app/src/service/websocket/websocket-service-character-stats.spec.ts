import { TestBed } from '@angular/core/testing';

import { WebsocketServiceCharacterStats } from './websocket-service-character-stats';

describe('WebsocketServiceCharacterStats', () => {
  let service: WebsocketServiceCharacterStats;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WebsocketServiceCharacterStats);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
