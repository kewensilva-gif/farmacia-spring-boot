import api from '../../../lib/axios'
import type { Role, CreateRoleRequest } from '../types/role.types'

export const roleService = {
  getAll: async (): Promise<Role[]> => {
    const response = await api.get<Role[]>('/api/roles')
    return response.data
  },

  getByUuid: async (uuid: string): Promise<Role> => {
    const response = await api.get<Role>(`/api/roles/${uuid}`)
    return response.data
  },

  create: async (data: CreateRoleRequest): Promise<Role> => {
    const response = await api.post<Role>('/api/roles', data)
    return response.data
  },

  update: async (uuid: string, data: CreateRoleRequest): Promise<Role> => {
    const response = await api.put<Role>(`/api/roles/${uuid}`, data)
    return response.data
  },

  delete: async (uuid: string): Promise<void> => {
    await api.delete(`/api/roles/${uuid}`)
  },
}
