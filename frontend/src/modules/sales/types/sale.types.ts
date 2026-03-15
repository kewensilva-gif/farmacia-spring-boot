export type PaymentMethod = 'CREDITCARD' | 'DEBITCARD' | 'PIX' | 'CASH'

export interface Sale {
  id: number
  totalPrice: number
  discount: number
  paymentMethod: PaymentMethod
  enabled: boolean
  employee: {
    id: number
    person: { firstname: string; lastname: string }
  }
  customer?: {
    id: number
    person: { firstname: string; lastname: string }
  }
}

export interface CreateSaleRequest {
  totalPrice: number
  discount: number
  paymentMethod: PaymentMethod
  employee: { id: number }
  customer?: { id: number }
}
