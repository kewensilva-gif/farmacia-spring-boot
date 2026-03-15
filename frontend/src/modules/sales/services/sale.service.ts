import api from '../../../lib/axios'
import type { Sale, CreateSaleRequest, PaymentMethod } from '../types/sale.types'

export const saleService = {
  getAll: async (): Promise<Sale[]> => {
    const response = await api.get<Sale[]>('/api/sales')
    return response.data
  },

  getById: async (id: number): Promise<Sale> => {
    const response = await api.get<Sale>(`/api/sales/${id}`)
    return response.data
  },

  create: async (data: CreateSaleRequest): Promise<Sale> => {
    const response = await api.post<Sale>('/api/sales', data)
    return response.data
  },

  update: async (id: number, data: CreateSaleRequest): Promise<Sale> => {
    const response = await api.put<Sale>(`/api/sales/${id}`, data)
    return response.data
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/api/sales/${id}`)
  },

  searchByPaymentMethod: async (method: PaymentMethod): Promise<Sale[]> => {
    const response = await api.get<Sale[]>(
      `/api/sales/search/payment-method?paymentMethod=${method}`,
    )
    return response.data
  },

  searchByPriceRange: async (minPrice: number, maxPrice: number): Promise<Sale[]> => {
    const response = await api.get<Sale[]>(
      `/api/sales/search/price?minPrice=${minPrice}&maxPrice=${maxPrice}`,
    )
    return response.data
  },
}
