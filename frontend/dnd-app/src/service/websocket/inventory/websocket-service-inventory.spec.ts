import { TestBed } from '@angular/core/testing';
import {  WebSocketServiceInventory } from './websocket-service-inventory';


describe('WebsocketService', () => {
  let service: WebSocketServiceInventory;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WebSocketServiceInventory);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
