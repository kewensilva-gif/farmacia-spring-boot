import api from '../../../lib/axios'
import type { Category, CreateCategoryRequest } from '../types/category.types'

export const categoryService = {
  getAll: async (): Promise<Category[]> => {
    const response = await api.get<Category[]>('/api/categories')
    return response.data
  },

  getById: async (id: number): Promise<Category> => {
    const response = await api.get<Category>(`/api/categories/${id}`)
    return response.data
  },

  create: async (data: CreateCategoryRequest): Promise<Category> => {
    const response = await api.post<Category>('/api/categories', data)
    return response.data
  },

  update: async (id: number, data: CreateCategoryRequest): Promise<Category> => {
    const response = await api.put<Category>(`/api/categories/${id}`, data)
    return response.data
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/api/categories/${id}`)
  },
}
