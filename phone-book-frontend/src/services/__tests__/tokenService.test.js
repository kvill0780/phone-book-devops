import { describe, it, expect, beforeEach } from 'vitest';
import tokenService from '../tokenService';

describe('tokenService', () => {
  beforeEach(() => {
    tokenService.clearToken();
  });

  it('sets and gets token', () => {
    expect(tokenService.getToken()).toBe(null);
    tokenService.setToken('abc');
    expect(tokenService.getToken()).toBe('abc');
  });

  it('clears token', () => {
    tokenService.setToken('123');
    tokenService.clearToken();
    expect(tokenService.getToken()).toBe(null);
  });
});
