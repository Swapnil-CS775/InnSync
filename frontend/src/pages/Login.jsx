import React, { useState } from 'react';
import api from '../api/client';
import { useNavigate, Link } from 'react-router-dom';

export default function Login() {
  const [username, setUsername] = useState(''); // backend expects username
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const nav = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await api.post('/api/auth/login', { username, password });
      // Postman collection showed token present. Try common fields:
      const token = res?.data?.token || res?.data?.accessToken || res?.data?.data?.token;
      if (!token) {
        console.log('Login response', res.data);
        alert('Login succeeded but token not found in response. Check console.');
        setLoading(false);
        nav('/');
        return;
      }
      localStorage.setItem('innsync_token', token);
      setLoading(false);
      nav('/');
    } catch (err) {
      setLoading(false);
      alert(err?.response?.data?.message || err.message || 'Login failed');
    }
  }

  return (
    <div style={{
      padding: '40px 50px',
      background: 'rgba(255, 255, 255, 0.1)',
      borderRadius: '16px',
      boxShadow: '0 8px 32px 0 rgba(0, 0, 0, 0.37)',
      backdropFilter: 'blur(15px)',
      border: '1px solid rgba(255, 255, 255, 0.18)',
      maxWidth: 420,
      width: '90%',
      color: '#ffffff',
      boxSizing: 'border-box'
    }}>
      <h2 style={{
        textAlign: 'center',
        marginBottom: '2rem',
        fontSize: '2.5rem',
        textShadow: '0 2px 4px rgba(0,0,0,0.3)',
      }}>
        Login
      </h2>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '20px' }}>
          <input
            required
            value={username}
            onChange={e => setUsername(e.target.value)}
            placeholder="Username or Phone"
            style={{
              width: '100%',
              padding: '15px',
              backgroundColor: 'rgba(0, 0, 0, 0.2)',
              border: '1px solid rgba(255, 255, 255, 0.2)',
              borderRadius: '8px',
              color: '#ffffff',
              fontSize: '1rem',
              outline: 'none',
              boxSizing: 'border-box',
              transition: 'border-color 0.3s ease'
            }}
          />
        </div>
        <div style={{ marginBottom: '20px' }}>
          <input
            required
            type="password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            placeholder="Password"
            style={{
              width: '100%',
              padding: '15px',
              backgroundColor: 'rgba(0, 0, 0, 0.2)',
              border: '1px solid rgba(255, 255, 255, 0.2)',
              borderRadius: '8px',
              color: '#ffffff',
              fontSize: '1rem',
              outline: 'none',
              boxSizing: 'border-box',
              transition: 'border-color 0.3s ease'
            }}
          />
        </div>
        <button
          type="submit"
          disabled={loading}
          style={{
            width: '100%',
            padding: '15px',
            border: 'none',
            borderRadius: '8px',
            fontSize: '1.1rem',
            fontWeight: 'bold',
            color: '#ffffff',
            marginTop: '10px',
            transition: 'all 0.3s ease',
            backgroundColor: '#007bff',
            cursor: loading ? 'not-allowed' : 'pointer',
            opacity: loading ? 0.6 : 1,
          }}
          onMouseEnter={(e) => { if (!loading) e.currentTarget.style.backgroundColor = '#0056b3'; }}
          onMouseLeave={(e) => { if (!loading) e.currentTarget.style.backgroundColor = '#007bff'; }}
        >
          {loading ? 'Logging in...' : 'Login'}
        </button>
      </form>
      <p style={{ marginTop: '2rem', textAlign: 'center', color: '#d1d1d1', fontSize: '0.9rem' }}>
        Don't have an account?{' '}
        <Link to="/register" style={{
          color: '#4dabf7',
          textDecoration: 'none',
          fontWeight: 'bold',
        }}
        onMouseEnter={(e) => e.currentTarget.style.textDecoration = 'underline'}
        onMouseLeave={(e) => e.currentTarget.style.textDecoration = 'none'}
        >
          Create one
        </Link>
      </p>
    </div>
  );
}