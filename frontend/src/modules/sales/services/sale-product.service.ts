import api from '../../../lib/axios'
import type { SaleProduct } from '../types/sale-product.types'

export const saleProductService = {
  getAll: async (): Promise<SaleProduct[]> => {
    const response = await api.get<SaleProduct[]>('/api/sale-products')
    return response.data
  },

  getById: async (id: number): Promise<SaleProduct> => {
    const response = await api.get<SaleProduct>(`/api/sale-products/${id}`)
    return response.data
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/api/sale-products/${id}`)
  },

  getBySaleId: async (saleId: number): Promise<SaleProduct[]> => {
    const response = await api.get<SaleProduct[]>(
      `/api/sale-products/search/sale?saleId=${saleId}`,
    )
    return response.data
  },

  getByProductId: async (productId: number): Promise<SaleProduct[]> => {
    const response = await api.get<SaleProduct[]>(
      `/api/sale-products/search/product?productId=${productId}`,
    )
    return response.data
  },
}
