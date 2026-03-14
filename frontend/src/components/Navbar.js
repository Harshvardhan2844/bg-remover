import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * Navbar Component
 * Shows the top navigation bar on every page.
 * - If logged in: shows username and logout button
 * - If logged out: shows Login and Sign Up buttons
 */
function Navbar() {
  const { user, isLoggedIn, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/'); // Go to home page after logout
  };

  return (
    <nav className="navbar">
      <div className="container navbar-content">

        {/* App Logo / Name */}
        <Link to="/" className="navbar-brand">
          BG<span>Remover</span>
        </Link>

        {/* Right side navigation links */}
        <div className="navbar-links">
          {isLoggedIn ? (
            // Show when logged in
            <>
              <span style={{ fontSize: '14px', color: '#555' }}>
                Hello, {user?.name}
              </span>
              <Link to="/dashboard" className="btn btn-primary" style={{ padding: '8px 16px', fontSize: '14px' }}>
                Dashboard
              </Link>
              <button
                onClick={handleLogout}
                className="btn btn-secondary"
                style={{ padding: '8px 16px', fontSize: '14px' }}
              >
                Logout
              </button>
            </>
          ) : (
            // Show when logged out
            <>
              <Link to="/login" className="btn btn-secondary" style={{ padding: '8px 16px', fontSize: '14px' }}>
                Login
              </Link>
              <Link to="/signup" className="btn btn-primary" style={{ padding: '8px 16px', fontSize: '14px' }}>
                Sign Up
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
