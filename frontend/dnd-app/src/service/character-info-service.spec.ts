import { TestBed } from '@angular/core/testing';

import { CharacterInfo } from './character-info-service';

describe('CharacterInfo', () => {
  let service: CharacterInfo;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CharacterInfo);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
