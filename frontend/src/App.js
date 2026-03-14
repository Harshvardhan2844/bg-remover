import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';
import Home from './pages/Home';
import Login from './pages/Login';
import Signup from './pages/Signup';
import Dashboard from './pages/Dashboard';
import OAuth2Callback from './pages/OAuth2Callback';

/**
 * App.js - Root component of the application.
 *
 * Sets up:
 * 1. AuthProvider - wraps everything so auth state is available everywhere
 * 2. Router - enables navigation between pages
 * 3. Routes - maps URLs to page components
 */
function App() {
  return (
    <AuthProvider>
      <Router>
        {/* Navbar is shown on every page */}
        <Navbar />

        {/* Page routes */}
        <Routes>
          {/* Public routes - anyone can access */}
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/oauth2/callback" element={<OAuth2Callback />} />

          {/* Protected route - only logged-in users can access */}
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            }
          />

          {/* Catch-all: redirect unknown URLs to home */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
