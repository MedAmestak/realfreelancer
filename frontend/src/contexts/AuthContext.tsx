'use client';

import React, { createContext, useContext, useState, useEffect, ReactNode, useCallback } from 'react';
import { jwtDecode } from 'jwt-decode';
import axiosInstance, { setGetAuthTokenFunction, setAuthTokenSetter } from '../utils/axiosInstance';

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
  logout: () => Promise<void>;
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
  const [token, setTokenState] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  const setToken = useCallback((newToken: string | null) => {
    setTokenState(newToken);
    if (newToken) {
      localStorage.setItem('token', newToken);
    } else {
      localStorage.removeItem('token');
        }
  }, []);
      
  useEffect(() => {
    setGetAuthTokenFunction(() => token);
    setAuthTokenSetter(setToken);
  }, [token, setToken]);

  const fetchUserProfile = useCallback(async () => {
    try {
      const response = await axiosInstance.get('/auth/profile');
      if (response.data) {
        setUser(response.data);
      }
    } catch (error: unknown) {
      console.error('Error fetching user profile, likely expired token.', error);
      setToken(null);
      setUser(null);
    } finally {
      setLoading(false);
    }
  }, [setToken]);

  useEffect(() => {
    const savedToken = localStorage.getItem('token');
    if (savedToken) {
      setTokenState(savedToken);
      fetchUserProfile();
    } else {
      setLoading(false);
    }
  }, [fetchUserProfile]);

  const login = async (email: string, password: string): Promise<{ success: boolean; error?: string }> => {
    try {
      const response = await axiosInstance.post('/auth/login', { email, password });

      if (response.data && response.data.accessToken) {
        const { accessToken } = response.data;
        setToken(accessToken);
        setGetAuthTokenFunction(() => accessToken);
        setAuthTokenSetter(setToken);
        const decoded: User = jwtDecode(accessToken);
        setUser(decoded);

        await fetchUserProfile();
        return { success: true };
      }
      return { success: false, error: 'Login failed: No access token received.' };
    } catch (error: unknown) {
      return { success: false, error: (error as any).response?.data?.message || 'Network error. Please check your connection.' };
    }
  };

  const register = async (userData: RegisterData): Promise<{ success: boolean; error?: string }> => {
    try {
      await axiosInstance.post('/auth/register', userData);
        // After successful registration, automatically log in the user
      return await login(userData.email, userData.password);
    } catch (error: unknown) {
        return { success: false, error: (error as any).response?.data?.message || 'Network error. Please check your connection.' };
    }
  };

  const logout = async () => {
    try {
      await axiosInstance.post('/auth/logout');
    } catch (error: unknown) {
        console.error("Logout failed", error);
    } finally {
    setToken(null);
    setUser(null);
    }
  };

  const getAuthToken = (): string | null => {
    return token;
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