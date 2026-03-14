import React, { useState, useRef } from 'react';
import { removeBackground } from '../services/api';
import { useAuth } from '../context/AuthContext';

/**
 * Dashboard Page
 * The main feature page where users can:
 * 1. Upload an image
 * 2. Remove its background
 * 3. Download the result
 */
function Dashboard() {
  const { user } = useAuth();

  // The image file the user selected
  const [selectedFile, setSelectedFile] = useState(null);

  // Preview URL for the selected image (shown before processing)
  const [previewUrl, setPreviewUrl] = useState(null);

  // The result image URL (after background removal)
  const [resultUrl, setResultUrl] = useState(null);

  // UI state
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [isDragOver, setIsDragOver] = useState(false);

  // Reference to the hidden file input element
  const fileInputRef = useRef(null);

  /**
   * When user selects a file (via button click or drag-and-drop),
   * create a preview URL and store the file.
   */
  const handleFileSelect = (file) => {
    if (!file) return;

    // Only allow image files
    if (!file.type.startsWith('image/')) {
      setError('Please select an image file (JPG, PNG, etc.)');
      return;
    }

    // Max file size: 10MB
    if (file.size > 10 * 1024 * 1024) {
      setError('File size must be less than 10MB.');
      return;
    }

    setError('');
    setSelectedFile(file);
    setResultUrl(null); // Clear previous result

    // Create a local URL to preview the image in the browser
    const url = URL.createObjectURL(file);
    setPreviewUrl(url);
  };

  /**
   * Handle file input change (click to browse).
   */
  const handleInputChange = (e) => {
    handleFileSelect(e.target.files[0]);
  };

  /**
   * Handle drag over event.
   */
  const handleDragOver = (e) => {
    e.preventDefault();
    setIsDragOver(true);
  };

  /**
   * Handle drag leave event.
   */
  const handleDragLeave = () => {
    setIsDragOver(false);
  };

  /**
   * Handle file drop.
   */
  const handleDrop = (e) => {
    e.preventDefault();
    setIsDragOver(false);
    handleFileSelect(e.dataTransfer.files[0]);
  };

  /**
   * Send image to backend to remove background.
   */
  const handleRemoveBackground = async () => {
    if (!selectedFile) {
      setError('Please select an image first.');
      return;
    }

    setLoading(true);
    setError('');
    setResultUrl(null);

    try {
      // Call the API - returns a blob (binary image data)
      const response = await removeBackground(selectedFile);

      // Convert blob to URL so we can display it
      const blob = new Blob([response.data], { type: 'image/png' });
      const url = URL.createObjectURL(blob);
      setResultUrl(url);

    } catch (err) {
      console.error('Error removing background:', err);
      setError(
        err.response?.data ||
        'Failed to remove background. Please check your remove.bg API key and try again.'
      );
    } finally {
      setLoading(false);
    }
  };

  /**
   * Download the processed image.
   */
  const handleDownload = () => {
    if (!resultUrl) return;

    // Create a temporary link and click it to trigger download
    const link = document.createElement('a');
    link.href = resultUrl;
    link.download = 'background-removed.png';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  /**
   * Reset everything to start fresh.
   */
  const handleReset = () => {
    setSelectedFile(null);
    setPreviewUrl(null);
    setResultUrl(null);
    setError('');
    // Reset the file input so same file can be selected again
    if (fileInputRef.current) fileInputRef.current.value = '';
  };

  return (
    <div className="dashboard-page">

      {/* Page header */}
      <h1>Background Remover</h1>
      <p>Upload an image and click "Remove Background" to get a transparent PNG.</p>

      {/* Error message */}
      {error && <div className="error-message">{error}</div>}

      {/* Upload area - only show if no image selected yet */}
      {!previewUrl && (
        <div
          className={`upload-area ${isDragOver ? 'drag-over' : ''}`}
          onClick={() => fileInputRef.current.click()}
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          onDrop={handleDrop}
        >
          <div style={{ fontSize: '40px', marginBottom: '12px' }}>📁</div>
          <p>Click to choose an image, or drag and drop it here</p>
          <p className="upload-hint">Supports JPG, PNG, WEBP — Max 10MB</p>

          {/* Hidden file input */}
          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            onChange={handleInputChange}
            style={{ display: 'none' }}
          />
        </div>
      )}

      {/* Action buttons - show when file is selected */}
      {previewUrl && !loading && (
        <div style={{ display: 'flex', gap: '12px', marginBottom: '20px', flexWrap: 'wrap' }}>
          <button
            className="btn btn-primary"
            onClick={handleRemoveBackground}
            disabled={loading}
          >
            ✨ Remove Background
          </button>
          <button
            className="btn btn-secondary"
            onClick={handleReset}
          >
            Choose Different Image
          </button>
        </div>
      )}

      {/* Loading indicator */}
      {loading && (
        <div className="loading-container">
          <div className="spinner"></div>
          <p>Removing background... This may take a few seconds.</p>
        </div>
      )}

      {/* Image preview boxes - show original and result side by side */}
      {previewUrl && !loading && (
        <div className="images-row">

          {/* Original image */}
          <div className="image-box">
            <div className="image-box-header">Original Image</div>
            <img src={previewUrl} alt="Original uploaded" />
            <div className="image-box-footer">
              <span style={{ fontSize: '13px', color: '#888' }}>
                {selectedFile?.name}
              </span>
            </div>
          </div>

          {/* Result image - show placeholder or result */}
          <div className="image-box">
            <div className="image-box-header">Background Removed</div>
            {resultUrl ? (
              <>
                <img src={resultUrl} alt="Background removed result" />
                <div className="image-box-footer">
                  <button
                    className="btn btn-primary btn-full"
                    onClick={handleDownload}
                  >
                    ⬇ Download PNG
                  </button>
                </div>
              </>
            ) : (
              <div style={{
                height: '260px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                flexDirection: 'column',
                color: '#aaa',
                backgroundColor: '#fafafa'
              }}>
                <div style={{ fontSize: '32px', marginBottom: '10px' }}>🖼️</div>
                <p style={{ fontSize: '14px' }}>Result will appear here</p>
              </div>
            )}
          </div>

        </div>
      )}

      {/* Tips section at the bottom */}
      <div style={{
        marginTop: '40px',
        padding: '20px',
        backgroundColor: '#f0f7ff',
        borderRadius: '8px',
        border: '1px solid #bfdbfe'
      }}>
        <h3 style={{ fontSize: '15px', fontWeight: '600', marginBottom: '8px', color: '#1e40af' }}>
          Tips for best results
        </h3>
        <ul style={{ fontSize: '14px', color: '#555', paddingLeft: '20px', lineHeight: '1.8' }}>
          <li>Use images with clear subjects (people, products, animals)</li>
          <li>Higher resolution images produce better results</li>
          <li>JPEG or PNG formats work best</li>
          <li>Make sure your remove.bg API key is set in the backend config</li>
        </ul>
      </div>

    </div>
  );
}

export default Dashboard;
