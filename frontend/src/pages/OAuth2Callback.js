import React, { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';

/**
 * OAuth2 Callback Page
 *
 * After Google/GitHub login, the backend redirects here with a JWT token in the URL.
 * Example URL: http://localhost:3000/oauth2/callback?token=eyJhbGci...
 *
 * This page:
 * 1. Reads the token from the URL
 * 2. Fetches user info using the token
 * 3. Saves the login state
 * 4. Redirects to dashboard
 */
function OAuth2Callback() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { login } = useAuth();

  useEffect(() => {
    const token = searchParams.get('token');

    if (token) {
      // Temporarily set token in localStorage so api.js interceptor can use it
      localStorage.setItem('token', token);

      // We decode the token to get user info (or could call /auth/me endpoint)
      // Simple approach: decode JWT payload (it's base64 encoded)
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const email = payload.sub; // 'sub' contains the email

        // Save login with basic info
        // In a real app, you'd call an /api/me endpoint to get full user details
        login({ name: email.split('@')[0], email: email }, token);
        navigate('/dashboard');
      } catch (err) {
        console.error('Failed to decode token:', err);
        navigate('/login');
      }
    } else {
      // No token in URL - something went wrong
      navigate('/login');
    }
  }, []);

  return (
    <div style={{ textAlign: 'center', padding: '80px 20px', color: '#555' }}>
      <div className="spinner" style={{ margin: '0 auto 20px' }}></div>
      <p>Completing login, please wait...</p>
    </div>
  );
}

export default OAuth2Callback;
