import axios from 'axios';

/**
 * API Service - Central place for all backend API calls.
 *
 * This file creates an axios instance configured to:
 * 1. Always point to our backend (localhost:8080)
 * 2. Automatically add the JWT token to every request
 */

// Create axios instance with base URL
const api = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor: runs before every request
// Adds the JWT token from localStorage to the Authorization header
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor: runs after every response
// If we get 401 (Unauthorized), the token expired - clear login
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid - log out
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// =============================================
// AUTH API FUNCTIONS
// =============================================

/** Register a new user */
export const registerUser = (name, email, password) => {
  return api.post('/auth/register', { name, email, password });
};

/** Login with email and password */
export const loginUser = (email, password) => {
  return api.post('/auth/login', { email, password });
};

// =============================================
// IMAGE API FUNCTIONS
// =============================================

/**
 * Upload image and remove its background.
 * Sends as multipart/form-data (required for file uploads).
 */
export const removeBackground = (imageFile) => {
  const formData = new FormData();
  formData.append('image', imageFile); // 'image' must match backend @RequestParam name

  return api.post('/api/remove-background', formData, {
    headers: {
      'Content-Type': 'multipart/form-data', // Override default JSON content type
    },
    responseType: 'blob', // Expect binary image data back (not JSON)
  });
};

export default api;
