export interface SaleProduct {
  id: number
  quantity: number
  unitPrice: number
  sale: { id: number }
  product: { id: number; name: string; barcode: string }
}
