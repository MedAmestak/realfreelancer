import { useState, useEffect } from 'react'

interface User {
  id: number
  username: string
  email: string
  githubLink?: string
  reputationPoints: number
}

interface AuthState {
  user: User | null
  token: string | null
  isAuthenticated: boolean
}

export function useAuth() {
  const [authState, setAuthState] = useState<AuthState>({
    user: null,
    token: null,
    isAuthenticated: false,
  })

  useEffect(() => {
    // Check for stored token on mount
    const token = localStorage.getItem('token')
    const user = localStorage.getItem('user')
    
    if (token && user) {
      try {
        const userData = JSON.parse(user)
        setAuthState({
          user: userData,
          token,
          isAuthenticated: true,
        })
      } catch (error) {
        console.error('Error parsing stored user data:', error)
        logout()
      }
    }
  }, [])

  const login = async (email: string, password: string) => {
    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
      })

      if (response.ok) {
        const data = await response.json()
        const userData = {
          id: data.userId,
          username: data.username,
          email: data.email,
          githubLink: data.githubLink,
          reputationPoints: data.reputationPoints,
        }

        localStorage.setItem('token', data.token)
        localStorage.setItem('user', JSON.stringify(userData))

        setAuthState({
          user: userData,
          token: data.token,
          isAuthenticated: true,
        })

        return { success: true }
      } else {
        const errorData = await response.json()
        return { success: false, error: errorData || 'Login failed' }
      }
    } catch (error) {
      console.error('Login error:', error)
      return { success: false, error: 'Network error' }
    }
  }

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
        const data = await response.json()
        const user = {
          id: data.userId,
          username: data.username,
          email: data.email,
          githubLink: data.githubLink,
          reputationPoints: data.reputationPoints,
        }

        localStorage.setItem('token', data.token)
        localStorage.setItem('user', JSON.stringify(user))

        setAuthState({
          user,
          token: data.token,
          isAuthenticated: true,
        })

        return { success: true }
      } else {
        const errorData = await response.json()
        return { success: false, error: errorData.message || 'Registration failed' }
      }
    } catch (error) {
      return { success: false, error: 'Network error' }
    }
  }

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setAuthState({
      user: null,
      token: null,
      isAuthenticated: false,
    })
  }

  const getAuthHeaders = () => {
    const token = localStorage.getItem('token')
    return token ? { Authorization: `Bearer ${token}` } : {}
  }

  return {
    user: authState.user,
    token: authState.token,
    isAuthenticated: authState.isAuthenticated,
    login,
    register,
    logout,
    getAuthHeaders,
  }
} 