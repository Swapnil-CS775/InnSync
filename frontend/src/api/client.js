// frontend/src/api/client.js
import axios from 'axios';

const api = axios.create({
  baseURL: '/', // relative so Vite proxy handles /api/auth -> localhost:8080
  withCredentials: false, // your backend returns JWT in body per Postman
  headers: { 'Content-Type': 'application/json' }
});

// Automatically attach token from localStorage
api.interceptors.request.use(cfg => {
  const token = localStorage.getItem('innsync_token');
  if (token) cfg.headers.Authorization = `Bearer ${token}`;
  return cfg;
});

export default api;
