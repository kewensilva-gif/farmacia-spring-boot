import React, { createContext, useContext, useState, useEffect } from 'react'
import { login as loginService, register as registerService } from '../modules/auth/services/auth.service'
import type { StoredUser } from '../modules/auth/types/auth.types'

interface AuthContextType {
  user: StoredUser | null
  token: string | null
  isLoading: boolean
  login: (login: string, password: string) => Promise<void>
  register: (username: string, email: string, password: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<StoredUser | null>(null)
  const [token, setToken] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const storedToken = localStorage.getItem('token')
    const storedUser = localStorage.getItem('user')
    if (storedToken && storedUser) {
      try {
        setToken(storedToken)
        setUser(JSON.parse(storedUser))
      } catch {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
      }
    }
    setIsLoading(false)
  }, [])

  const login = async (loginInput: string, password: string) => {
    const result = await loginService({ login: loginInput, password })
    const storedUser: StoredUser = {
      username: result.username,
      email: result.email,
      role: result.role,
    }
    setToken(result.token)
    setUser(storedUser)
    localStorage.setItem('token', result.token)
    localStorage.setItem('user', JSON.stringify(storedUser))
  }

  const register = async (username: string, email: string, password: string) => {
    const result = await registerService({ username, email, password })
    const storedUser: StoredUser = {
      username: result.username,
      email: result.email,
      role: result.role,
    }
    setToken(result.token)
    setUser(storedUser)
    localStorage.setItem('token', result.token)
    localStorage.setItem('user', JSON.stringify(storedUser))
  }

  const logout = () => {
    setToken(null)
    setUser(null)
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return (
    <AuthContext.Provider value={{ user, token, isLoading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}
