import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * ProtectedRoute Component
 *
 * Wraps routes that require login.
 * If user is NOT logged in, redirects to /login.
 * If user IS logged in, shows the protected page.
 *
 * Usage in App.js:
 * <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
 */
function ProtectedRoute({ children }) {
  const { isLoggedIn, loading } = useAuth();

  // Wait until we check localStorage before deciding
  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '60px', color: '#666' }}>
        Loading...
      </div>
    );
  }

  // If not logged in, redirect to login page
  if (!isLoggedIn) {
    return <Navigate to="/login" replace />;
  }

  // If logged in, show the page
  return children;
}

export default ProtectedRoute;
