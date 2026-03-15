import api from '../../../lib/axios'
import type { AuthRequest, RegisterRequest, AuthResponse } from '../types/auth.types'

export interface LoginResult {
  token: string
  username: string
  email: string
  role: string
}

export async function login(data: AuthRequest): Promise<LoginResult> {
  const response = await api.post<AuthResponse>('/api/auth/login', data)
  const { token, username, email } = response.data

  const userResponse = await api.get(`/api/users/search/username?username=${encodeURIComponent(username)}`, {
    headers: { Authorization: `Bearer ${token}` },
  })

  const role: string = userResponse.data?.role?.name || 'CUSTOMER'

  return { token, username, email, role }
}

export async function register(data: RegisterRequest): Promise<LoginResult> {
  const response = await api.post<AuthResponse>('/api/auth/register', data)
  const { token, username, email } = response.data

  const userResponse = await api.get(`/api/users/search/username?username=${encodeURIComponent(username)}`, {
    headers: { Authorization: `Bearer ${token}` },
  })

  const role: string = userResponse.data?.role?.name || 'CUSTOMER'

  return { token, username, email, role }
}
