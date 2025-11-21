// Simple in-memory token store used by the frontend.
// This reduces repeated access to localStorage at request time and centralizes token handling.
// NOTE: For best security the refresh token should be stored in a secure, HttpOnly cookie
// and the server should expose a /auth/refresh endpoint that reads that cookie. That
// change requires server-side work; see SECURITY.md and next steps in the project plan.

let _token = null;

export const setToken = (token) => {
  _token = token;
};

export const getToken = () => _token;

export const clearToken = () => {
  _token = null;
};

export default {
  setToken,
  getToken,
  clearToken,
};
