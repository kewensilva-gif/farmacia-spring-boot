import api from '../../../lib/axios'
import type { User, UserDto, CreateUserRequest, UserRegistrationRequest } from '../types/user.types'

export const userService = {
  getAll: async (): Promise<User[]> => {
    const response = await api.get<User[]>('/api/users')
    return response.data
  },

  getByUuid: async (uuid: string): Promise<User> => {
    const response = await api.get<User>(`/api/users/${uuid}`)
    return response.data
  },

  create: async (data: CreateUserRequest): Promise<User> => {
    const response = await api.post<User>('/api/users', data)
    return response.data
  },

  update: async (uuid: string, data: UserDto): Promise<User> => {
    const response = await api.put<User>(`/api/users/${uuid}`, data)
    return response.data
  },

  delete: async (uuid: string): Promise<void> => {
    await api.delete(`/api/users/${uuid}`)
  },

  getEnabled: async (): Promise<User[]> => {
    const response = await api.get<User[]>('/api/users/enabled')
    return response.data
  },

  getDisabled: async (): Promise<User[]> => {
    const response = await api.get<User[]>('/api/users/disabled')
    return response.data
  },

  searchByUsername: async (username: string): Promise<User> => {
    const response = await api.get<User>(
      `/api/users/search/username?username=${encodeURIComponent(username)}`,
    )
    return response.data
  },

  adminRegister: async (data: UserRegistrationRequest): Promise<User> => {
    const response = await api.post<User>('/api/admin/register', data)
    return response.data
  },
}
