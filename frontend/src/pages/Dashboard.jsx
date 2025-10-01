import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

export default function Dashboard() {
  const nav = useNavigate();
  const [isMobile, setIsMobile] = useState(window.innerWidth < 768);

  // This effect handles the responsive layout change
  useEffect(() => {
    function handleResize() {
      setIsMobile(window.innerWidth < 768);
    }
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  function logout() {
    localStorage.removeItem('innsync_token');
    nav('/login');
  }

  // --- STYLES ---

  const dashboardContainerStyle = {
    display: 'flex',
    flexDirection: isMobile ? 'column' : 'row',
    minHeight: '100vh',
    width: '100vw',
    color: '#ffffff',
  };

  const navBarStyle = {
    background: 'rgba(255, 255, 255, 0.05)',
    backdropFilter: 'blur(20px)',
    borderRight: isMobile ? 'none' : '1px solid rgba(255, 255, 255, 0.18)',
    borderBottom: isMobile ? '1px solid rgba(255, 255, 255, 0.18)' : 'none',
    padding: '20px',
    boxSizing: 'border-box',
    width: isMobile ? '100%' : '250px',
    display: 'flex',
    flexDirection: 'column',
  };

  const contentAreaStyle = {
    flex: 1,
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    padding: '40px',
    boxSizing: 'border-box',
  };

  const dashboardCardStyle = {
    padding: '40px 50px',
    background: 'rgba(255, 255, 255, 0.1)',
    borderRadius: '16px',
    boxShadow: '0 8px 32px 0 rgba(0, 0, 0, 0.37)',
    backdropFilter: 'blur(15px)',
    border: '1px solid rgba(255, 255, 255, 0.18)',
    textAlign: 'center',
    maxWidth: '500px',
    width: '100%',
  };

  const buttonStyle = {
    backgroundColor: '#e74c3c',
    color: '#ffffff',
    border: 'none',
    borderRadius: '50px',
    padding: '15px 35px',
    fontSize: '1rem',
    fontWeight: '600',
    cursor: 'pointer',
    boxShadow: '0 5px 15px rgba(231, 76, 60, 0.4)',
    transition: 'transform 0.2s ease, box-shadow 0.2s ease',
  };

  return (
    <div style={dashboardContainerStyle}>
      {/* Left Side Navigation */}
      <nav style={navBarStyle}>
        <h1 style={{ fontSize: '1.8rem', fontWeight: 'bold', marginBottom: '2rem' }}>
          InnSync
        </h1>
        <div style={{ flex: 1 }}>
          {/* Menu items can go here in the future */}
        </div>
        <p style={{
          fontSize: '0.9rem',
          fontStyle: 'italic',
          color: 'rgba(255, 255, 255, 0.5)',
          textAlign: 'center',
          padding: '15px',
          border: '1px dashed rgba(255, 255, 255, 0.3)',
          borderRadius: '8px',
        }}>
          yet more to come....
        </p>
      </nav>

      {/* Main Content Area */}
      <main style={contentAreaStyle}>
        <div style={dashboardCardStyle}>
          <h2 style={{
            fontSize: '2.5rem',
            fontWeight: 'bold',
            marginBottom: '1rem',
            textShadow: '0 2px 4px rgba(0,0,0,0.3)',
          }}>
            Dashboard
            <span style={{
              display: 'block',
              fontSize: '1rem',
              fontWeight: '300',
              color: '#d1d1d1',
              marginTop: '8px'
            }}>
              (Protected)
            </span>
          </h2>
          <p style={{
            fontSize: '1.1rem',
            lineHeight: '1.6',
            color: '#e0e0e0',
            marginBottom: '2.5rem'
          }}>
            You are logged in if you can see this.
          </p>
          <button
            onClick={logout}
            style={buttonStyle}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = 'translateY(-3px)';
              e.currentTarget.style.boxShadow = '0 8px 25px rgba(231, 76, 60, 0.5)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = 'translateY(0)';
              e.currentTarget.style.boxShadow = '0 5px 15px rgba(231, 76, 60, 0.4)';
            }}
          >
            Logout
          </button>
        </div>
      </main>
    </div>
  );
}