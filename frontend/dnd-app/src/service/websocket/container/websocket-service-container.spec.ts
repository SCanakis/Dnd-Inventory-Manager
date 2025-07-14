import { TestBed } from '@angular/core/testing';
import { WebsocketServiceContainer } from './websocket-service-container';


describe('WebsocketServiceContainer', () => {
  let service: WebsocketServiceContainer;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WebsocketServiceContainer);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
