import axios from 'axios';
import tokenService from './tokenService';

const API_BASE_URL = process.env.NODE_ENV === 'production' 
  ? '/api' 
  : 'http://localhost:8080/api';

// Performance monitoring
const performanceLog = (method, url, duration, status) => {
  if (process.env.NODE_ENV === 'development') {
    console.log(`🚀 API ${method.toUpperCase()} ${url}: ${duration.toFixed(2)}ms (${status})`);
  }
  
  if (duration > 3000) {
    console.warn(`⚠️ Slow API call: ${method.toUpperCase()} ${url} took ${duration.toFixed(2)}ms`);
  }
};

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Intercepteur pour ajouter l'authentification Bearer Token et monitoring
api.interceptors.request.use((config) => {
  // Add performance timing
  config.metadata = { startTime: performance.now() };
  // Ne pas ajouter d'authentification pour les routes /auth/
  if (config.url?.includes('/auth/')) {
    return config;
  }
  
  // Prefer reading the token from the in-memory tokenService for performance and
  // to centralize access. For compatibility the app still persists userData in
  // localStorage; tokenService is initialized at app startup in AuthContext.
  const token = tokenService.getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  
  return config;
}, (error) => {
  return Promise.reject(error);
});

// Intercepteur pour gérer les erreurs d'authentification avec refresh token
api.interceptors.response.use(
  (response) => {
    // Log performance
    if (response.config.metadata) {
      const duration = performance.now() - response.config.metadata.startTime;
      performanceLog(response.config.method, response.config.url, duration, response.status);
    }
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    
    if (error.response?.status === 401 && !originalRequest._retry && !originalRequest.url?.includes('/auth/')) {
      originalRequest._retry = true;
      
      try {
        const userData = localStorage.getItem('userData');
        if (userData) {
          const user = JSON.parse(userData);
          if (user.refreshToken) {
            const refreshResponse = await api.post('/auth/refresh', { refreshToken: user.refreshToken });
            
            if (refreshResponse.data && refreshResponse.data.token) {
              const newUserData = {
                username: refreshResponse.data.username,
                token: refreshResponse.data.token,
                refreshToken: refreshResponse.data.refreshToken,
                expiresIn: refreshResponse.data.expiresIn,
              };
              
              // update persisted user and in-memory token
              localStorage.setItem('userData', JSON.stringify(newUserData));
              tokenService.setToken(refreshResponse.data.token);
              originalRequest.headers.Authorization = `Bearer ${refreshResponse.data.token}`;
              
              return api(originalRequest);
            }
          }
        }
      } catch (refreshError) {
        console.error('Token refresh failed:', refreshError);
      }
      
      // Si le refresh échoue, rediriger vers login
      const currentPath = window.location.pathname;
      if (currentPath !== '/login' && currentPath !== '/register') {
        localStorage.removeItem('userData');
        setTimeout(() => {
          window.location.href = '/login';
        }, 100);
      }
    }
    
    return Promise.reject(error);
  }
);

export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
  refresh: (refreshData) => api.post('/auth/refresh', refreshData),
};

export const contactAPI = {
  getAll: () => api.get('/contacts'),
  getById: (id) => api.get(`/contacts/${id}`),
  create: (contact) => api.post('/contacts', contact),
  update: (id, contact) => api.put(`/contacts/${id}`, contact),
  delete: (id) => api.delete(`/contacts/${id}`),
  search: (query) => api.get('/contacts/search', { params: { query: encodeURIComponent(query) } }),
  searchByPhone: (phone) => api.get('/contacts/search/phone', { params: { phoneNumber: encodeURIComponent(phone) } }),
  searchByFirstName: (firstName) => api.get('/contacts/search/firstname', { params: { firstName: encodeURIComponent(firstName) } }),
  searchByLastName: (lastName) => api.get('/contacts/search/lastname', { params: { lastName: encodeURIComponent(lastName) } }),
  getByGroup: (groupId) => api.get(`/contacts/group/${groupId}`),
};

export const groupAPI = {
  getAll: () => api.get('/groups'),
  create: (group) => api.post('/groups', group),
  update: (id, group) => api.put(`/groups/${id}`, group),
  delete: (id) => api.delete(`/groups/${id}`),
};

export default api;