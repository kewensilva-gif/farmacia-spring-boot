import api from '../../../lib/axios'
import type { Customer, CustomerDto } from '../types/customer.types'

export const customerService = {
  getAll: async (): Promise<Customer[]> => {
    const response = await api.get<Customer[]>('/api/customers')
    return response.data
  },

  getById: async (id: number): Promise<Customer> => {
    const response = await api.get<Customer>(`/api/customers/${id}`)
    return response.data
  },

  create: async (data: CustomerDto): Promise<Customer> => {
    const response = await api.post<Customer>('/api/customers', data)
    return response.data
  },

  update: async (id: number, data: CustomerDto): Promise<Customer> => {
    const response = await api.put<Customer>(`/api/customers/${id}`, data)
    return response.data
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/api/customers/${id}`)
  },

  searchByDateRange: async (startDate: string, endDate: string): Promise<Customer[]> => {
    const response = await api.get<Customer[]>(
      `/api/customers/search/date?startDate=${startDate}&endDate=${endDate}`,
    )
    return response.data
  },
}
