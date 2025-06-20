'use client';

import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';

interface User {
  id: number;
  username: string;
  email: string;
  bio?: string;
  skills: string[];
  reputationPoints: number;
  isVerified: boolean;
}

interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (username: string, password: string) => Promise<boolean>;
  logout: () => void;
  register: (userData: RegisterData) => Promise<boolean>;
  loading: boolean;
}

interface RegisterData {
  username: string;
  email: string;
  password: string;
  bio?: string;
  skills?: string[];
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check for existing token on app load
    const savedToken = localStorage.getItem('token');
    if (savedToken) {
      setToken(savedToken);
      fetchUserProfile(savedToken);
    } else {
      setLoading(false);
    }
  }, []);

  const fetchUserProfile = async (authToken: string) => {
    try {
      const response = await fetch('http://localhost:8080/api/auth/profile', {
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      });
      
      if (response.ok) {
        const userData = await response.json();
        setUser(userData);
      } else {
        // Token is invalid, clear it
        localStorage.removeItem('token');
        setToken(null);
      }
    } catch (error) {
      console.error('Error in user profile fetch');
      localStorage.removeItem('token');
      setToken(null);
    } finally {
      setLoading(false);
    }
  };

  const login = async (username: string, password: string): Promise<boolean> => {
    try {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email: username, password }),
      });

      if (response.ok) {
        const data = await response.json();
        const authToken = data.token;
        
        localStorage.setItem('token', authToken);
        setToken(authToken);
        
        // Fetch user profile
        await fetchUserProfile(authToken);
        return true;
      } else {
        const errorData = await response.json();
        // Safely log error without exposing sensitive data
        console.error('Login failed: Invalid credentials');
        return false;
      }
    } catch (error) {
      // Safely log error without exposing sensitive data
      console.error('Network error during login');
      return false;
    }
  };

  const register = async (userData: RegisterData): Promise<boolean> => {
    try {
      const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
      });

      if (response.ok) {
        const data = await response.json();
        const authToken = data.token;
        
        localStorage.setItem('token', authToken);
        setToken(authToken);
        
        // Fetch user profile
        await fetchUserProfile(authToken);
        return true;
      } else {
        const errorData = await response.json();
        // Safely log error without exposing sensitive data
        console.error('Registration failed: Invalid data');
        return false;
      }
    } catch (error) {
      // Safely log error without exposing sensitive data
      console.error('Network error during registration');
      return false;
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    setToken(null);
    setUser(null);
  };

  const value: AuthContextType = {
    user,
    token,
    login,
    logout,
    register,
    loading,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}; 