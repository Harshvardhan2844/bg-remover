import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * Home Page
 * The first page users see when they visit the app.
 * Shows a description and buttons to get started.
 */
function Home() {
  const { isLoggedIn } = useAuth();

  return (
    <div className="home-page">

      {/* Main heading */}
      <h1>
        Remove Image Backgrounds<br />
        <span>In Seconds</span>
      </h1>

      {/* Short description */}
      <p>
        Upload any photo and our AI will automatically remove the background.
        Download a clean PNG with a transparent background — free and easy.
      </p>

      {/* Action buttons */}
      <div className="home-buttons">
        {isLoggedIn ? (
          <Link to="/dashboard" className="btn btn-primary btn-large">
            Go to Dashboard
          </Link>
        ) : (
          <>
            <Link to="/signup" className="btn btn-primary btn-large">
              Get Started — It's Free
            </Link>
            <Link to="/login" className="btn btn-secondary btn-large">
              Login
            </Link>
          </>
        )}
      </div>

      {/* Feature highlights */}
      <div className="features-grid">
        <div className="feature-item">
          <div className="feature-icon">⚡</div>
          <h3>Fast Processing</h3>
          <p>Background removed in just a few seconds using AI.</p>
        </div>
        <div className="feature-item">
          <div className="feature-icon">🎯</div>
          <h3>High Quality</h3>
          <p>Clean edges, precise cutouts, even for complex images.</p>
        </div>
        <div className="feature-item">
          <div className="feature-icon">📥</div>
          <h3>Free Download</h3>
          <p>Download your result as a transparent PNG instantly.</p>
        </div>
      </div>

    </div>
  );
}

export default Home;
