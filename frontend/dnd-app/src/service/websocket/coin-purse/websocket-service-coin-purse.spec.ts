import { TestBed } from '@angular/core/testing';
import { WebsocketServiceCoinPurse } from './websocket-service-coin-purse';


describe('WebsocketServiceCoinPurse', () => {
  let service: WebsocketServiceCoinPurse;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WebsocketServiceCoinPurse);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
