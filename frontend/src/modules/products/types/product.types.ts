export interface Product {
  id: number
  name: string
  unitPrice: number
  barcode: string
  stockQuantity: number
  expirationDate: string
  pathImage?: string
  enabled: boolean
  category: { id: number; name: string }
}

export interface CreateProductRequest {
  name: string
  unitPrice: number
  barcode: string
  stockQuantity: number
  expirationDate: string
  category: { id: number }
}
