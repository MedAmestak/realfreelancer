import { useContext } from 'react';
import { AuthContext } from '../src/contexts/AuthContext';

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }

  // Custom register function that uses context's login after registration
  const register = async (userData: {
    username: string
    email: string
    password: string
    githubLink?: string
    skills?: string[]
    bio?: string
  }) => {
    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/auth/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
      })

      if (response.ok) {
        // After successful registration, log in the user
        await context.login(userData.email, userData.password);
        return { success: true };
      } else {
        let errorMessage = 'Registration failed';
        try {
          const errorData = await response.json();
          errorMessage = errorData.message || errorData.error || errorMessage;
        } catch (err) {
          // If not JSON, try to get text
          try {
            errorMessage = await response.text();
          } catch {}
        }
        return { success: false, error: errorMessage };
      }
    } catch (error) {
      return { success: false, error: 'Network error' }
    }
  };

  return {
    ...context,
    register,
  };
} 