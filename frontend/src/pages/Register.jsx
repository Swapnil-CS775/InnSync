// frontend/src/pages/Register.jsx
import React, { useState } from 'react';
import api from '../api/client';
import { useNavigate, Link } from 'react-router-dom';

export default function Register() {
  const nav = useNavigate();

  // owner fields
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');

  // business fields
  const [businessName, setBusinessName] = useState('');
  const [businessType, setBusinessType] = useState('CAFE'); // default
  const [addressLine1, setAddressLine1] = useState('');
  const [city, setCity] = useState('');
  const [stateVal, setStateVal] = useState('');
  const [pinCode, setPinCode] = useState('');
  const [gstNumber, setGstNumber] = useState('');

  const [loading, setLoading] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setLoading(true);

    // payload matches Postman RegisterOwner example
    const payload = {
      owner: {
        fullName,
        email,
        phone,
        password
      },
      business: {
        businessName,
        businessType,
        addressLine1,
        city,
        state: stateVal,
        pinCode,
        gstNumber
      }
    };

    try {
      const res = await api.post('/api/auth/register', payload);
      // success might return created owner — check response and redirect to login
      alert('Registration successful. Please login.');
      setLoading(false);
      nav('/login');
    } catch (err) {
      setLoading(false);
      // show server error message if present
      const msg = err?.response?.data?.message || JSON.stringify(err?.response?.data) || err.message;
      alert('Registration failed: ' + msg);
      console.error('Register error', err);
    }
  }

  // Common styles for form elements to avoid repetition
  const inputStyle = {
    width: '100%',
    padding: '12px 15px',
    backgroundColor: 'rgba(0, 0, 0, 0.2)',
    border: '1px solid rgba(255, 255, 255, 0.2)',
    borderRadius: '8px',
    color: '#ffffff',
    fontSize: '1rem',
    outline: 'none',
    boxSizing: 'border-box',
  };

  const fieldsetStyle = {
    border: '1px solid rgba(255, 255, 255, 0.3)',
    borderRadius: '8px',
    padding: '25px',
    marginBottom: '2rem',
    display: 'grid',
    gridTemplateColumns: '1fr 1fr',
    gap: '15px 20px',
  };

  const fullWidthField = {
    ...inputStyle,
    gridColumn: '1 / -1',
  };

  return (
    <div style={{
      maxWidth: 700,
      width: '90%',
      margin: 'auto',
      padding: '40px 50px',
      background: 'rgba(255, 255, 255, 0.1)',
      borderRadius: '16px',
      boxShadow: '0 8px 32px 0 rgba(0, 0, 0, 0.37)',
      backdropFilter: 'blur(15px)',
      border: '1px solid rgba(255, 255, 255, 0.18)',
      color: '#ffffff',
      boxSizing: 'border-box',
    }}>
      <h2 style={{ textAlign: 'center', marginBottom: '2rem', fontSize: '2.5rem' }}>
        Create Your Account
      </h2>
      <form onSubmit={handleSubmit}>
        <fieldset style={fieldsetStyle}>
          <legend style={{ fontSize: '1.2rem', fontWeight: 'bold', padding: '0 10px', marginLeft: '10px' }}>Owner Details</legend>
          <input required placeholder="Full Name" value={fullName} onChange={e => setFullName(e.target.value)} style={inputStyle} />
          <input required type="email" placeholder="Email" value={email} onChange={e => setEmail(e.target.value)} style={inputStyle} />
          <input required placeholder="Phone" value={phone} onChange={e => setPhone(e.target.value)} style={inputStyle} />
          <input required type="password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} style={inputStyle} />
        </fieldset>

        <fieldset style={fieldsetStyle}>
          <legend style={{ fontSize: '1.2rem', fontWeight: 'bold', padding: '0 10px', marginLeft: '10px' }}>Business Details</legend>
          <input required placeholder="Business Name" value={businessName} onChange={e => setBusinessName(e.target.value)} style={fullWidthField} />
          <select value={businessType} onChange={e => setBusinessType(e.target.value)} style={fullWidthField}>
            <option style={{backgroundColor: '#333'}} value="CAFE">CAFE</option>
            <option style={{backgroundColor: '#333'}} value="RESTAURANT">RESTAURANT</option>
            <option style={{backgroundColor: '#333'}} value="HOTEL">HOTEL</option>
            <option style={{backgroundColor: '#333'}} value="OTHER">OTHER</option>
          </select>
          <input placeholder="Address Line 1" value={addressLine1} onChange={e => setAddressLine1(e.target.value)} style={fullWidthField} />
          <input placeholder="City" value={city} onChange={e => setCity(e.target.value)} style={inputStyle} />
          <input placeholder="State" value={stateVal} onChange={e => setStateVal(e.target.value)} style={inputStyle} />
          <input placeholder="Pin Code" value={pinCode} onChange={e => setPinCode(e.target.value)} style={inputStyle} />
          <input placeholder="GST Number (Optional)" value={gstNumber} onChange={e => setGstNumber(e.target.value)} style={inputStyle} />
        </fieldset>

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
            backgroundColor: '#28a745', // Green for register
            cursor: loading ? 'not-allowed' : 'pointer',
            opacity: loading ? 0.6 : 1,
          }}
          onMouseEnter={(e) => { if (!loading) e.currentTarget.style.backgroundColor = '#218838'; }}
          onMouseLeave={(e) => { if (!loading) e.currentTarget.style.backgroundColor = '#28a745'; }}
        >
          {loading ? 'Registering...' : 'Register'}
        </button>
      </form>

      <p style={{ marginTop: '2rem', textAlign: 'center', fontSize: '0.9rem' }}>
        Already have an account?{' '}
        <Link to="/login" style={{ color: '#4dabf7', textDecoration: 'none', fontWeight: 'bold' }}
          onMouseEnter={(e) => e.currentTarget.style.textDecoration = 'underline'}
          onMouseLeave={(e) => e.currentTarget.style.textDecoration = 'none'}
        >
          Login Here
        </Link>
      </p>
    </div>
  );
}