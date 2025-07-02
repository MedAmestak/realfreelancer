'use client';

import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { jwtDecode } from 'jwt-decode';

export type User = {
  id: number;
  username: string;
  email: string;
  bio?: string;
  skills: string[];
  reputationPoints: number;
  isVerified: boolean;
};

interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (username: string, password: string) => Promise<{ success: boolean; error?: string }>;
  logout: () => void;
  register: (userData: RegisterData) => Promise<{ success: boolean; error?: string }>;
  loading: boolean;
  getAuthToken: () => string | null;
  setUser: React.Dispatch<React.SetStateAction<User | null>>;
}

interface RegisterData {
  username: string;
  email: string;
  password: string;
  bio?: string;
  skills?: string[];
}

// Add this interface for the JWT payload
type DecodedToken = {
  userId: number;
  username: string;
  email: string;
  bio?: string;
  skills: string[];
  reputationPoints: number;
  isVerified: boolean;
  // Add other fields as needed
};

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

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

const parseErrorResponse = async (response: Response): Promise<string> => {
  const defaultError = "An unexpected error occurred. Please try again.";
  try {
    const errorData = await response.json();
    if (errorData) {
      if (typeof errorData === 'object' && Object.keys(errorData).length > 0) {
        const messages = Object.values(errorData);
        if (messages.every(m => typeof m === 'string')) {
          return messages.join('. ');
        }
      }
      if (errorData.message) return errorData.message;
      if (errorData.error) return errorData.error;
    }
    const textError = await response.text();
    return textError || defaultError;
  } catch (err) {
    try {
      const textError = await response.text();
      return textError || defaultError;
    } catch (textErr) {
      return defaultError;
    }
  }
};

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

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

  const login = async (username: string, password: string): Promise<{ success: boolean; error?: string }> => {
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
        // Decode the JWT to get user info
        const decoded: DecodedToken = jwtDecode(authToken);
        setUser({
          id: decoded.userId,
          username: decoded.username,
          email: decoded.email,
          bio: decoded.bio,
          skills: decoded.skills,
          reputationPoints: decoded.reputationPoints,
          isVerified: decoded.isVerified,
        });
        await fetchUserProfile(authToken);
        return { success: true };
      } else {
        const errorMessage = await parseErrorResponse(response);
        return { success: false, error: errorMessage };
      }
    } catch (error) {
      return { success: false, error: 'Network error. Please check your connection.' };
    }
  };

  const register = async (userData: RegisterData): Promise<{ success: boolean; error?: string }> => {
    try {
      const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
      });

      if (response.ok) {
        // After successful registration, automatically log in the user
        const loginResult = await login(userData.email, userData.password);
        return loginResult;
      } else {
        const errorMessage = await parseErrorResponse(response);
        return { success: false, error: errorMessage };
      }
    } catch (error) {
      return { success: false, error: 'Network error. Please check your connection.' };
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    setToken(null);
    setUser(null);
  };

  const getAuthToken = (): string | null => {
    return localStorage.getItem('token');
  };

  const value: AuthContextType = {
    user,
    token,
    login,
    logout,
    register,
    loading,
    getAuthToken,
    setUser,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}; 