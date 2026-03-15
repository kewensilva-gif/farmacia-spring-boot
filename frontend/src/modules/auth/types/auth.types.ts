export interface AuthRequest {
  login: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
}

export interface AuthResponse {
  token: string
  username: string
  email: string
}

export interface StoredUser {
  username: string
  email: string
  role: string
}
