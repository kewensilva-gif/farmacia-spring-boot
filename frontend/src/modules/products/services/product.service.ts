import api from '../../../lib/axios'
import type { Product, CreateProductRequest } from '../types/product.types'

export const productService = {
  getAll: async (): Promise<Product[]> => {
    const response = await api.get<Product[]>('/api/products')
    return response.data
  },

  getById: async (id: number): Promise<Product> => {
    const response = await api.get<Product>(`/api/products/${id}`)
    return response.data
  },

  create: async (data: CreateProductRequest): Promise<Product> => {
    const response = await api.post<Product>('/api/products', data)
    return response.data
  },

  update: async (id: number, data: CreateProductRequest): Promise<Product> => {
    const response = await api.put<Product>(`/api/products/${id}`, data)
    return response.data
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/api/products/${id}`)
  },

  searchByName: async (name: string): Promise<Product[]> => {
    const response = await api.get<Product[]>(`/api/products/search/name?name=${encodeURIComponent(name)}`)
    return response.data
  },

  searchByBarcode: async (barcode: string): Promise<Product[]> => {
    const response = await api.get<Product[]>(`/api/products/search/barcode?barcode=${encodeURIComponent(barcode)}`)
    return response.data
  },

  getExpired: async (): Promise<Product[]> => {
    const response = await api.get<Product[]>('/api/products/expired')
    return response.data
  },

  getLowStock: async (): Promise<Product[]> => {
    const response = await api.get<Product[]>('/api/products/low-stock')
    return response.data
  },
}
