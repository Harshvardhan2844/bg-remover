import React, { createContext, useContext, useState, useEffect } from 'react';

/**
 * AuthContext - Global state for authentication.
 *
 * This context stores:
 * - Whether the user is logged in
 * - The user's info (name, email)
 * - The JWT token
 *
 * Any component can read/update auth state using useAuth() hook.
 */

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);       // User info object
  const [token, setToken] = useState(null);     // JWT token
  const [loading, setLoading] = useState(true); // Loading while checking localStorage

  // On app start, check if user was previously logged in
  useEffect(() => {
    const savedToken = localStorage.getItem('token');
    const savedUser = localStorage.getItem('user');

    if (savedToken && savedUser) {
      setToken(savedToken);
      setUser(JSON.parse(savedUser));
    }
    setLoading(false); // Done checking
  }, []);

  /**
   * Call this after successful login/register.
   * Saves token and user info to localStorage so login persists.
   */
  const login = (userData, jwtToken) => {
    setUser(userData);
    setToken(jwtToken);
    localStorage.setItem('token', jwtToken);
    localStorage.setItem('user', JSON.stringify(userData));
  };

  /**
   * Call this to log out the user.
   * Clears everything from state and localStorage.
   */
  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  const isLoggedIn = !!token; // true if token exists

  return (
    <AuthContext.Provider value={{ user, token, isLoggedIn, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
}

// Custom hook - makes it easy to use auth in any component
// Usage: const { user, isLoggedIn, login, logout } = useAuth();
export function useAuth() {
  return useContext(AuthContext);
}
