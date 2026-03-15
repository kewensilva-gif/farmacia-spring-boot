export function formatCurrency(value: number): string {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  }).format(value)
}

export function formatDate(date: string): string {
  if (!date) return '-'
  const d = new Date(date)
  if (isNaN(d.getTime())) return date
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  }).format(d)
}

export function formatPaymentMethod(method: string): string {
  const map: Record<string, string> = {
    CREDITCARD: 'Cartão de Crédito',
    DEBITCARD: 'Cartão de Débito',
    PIX: 'Pix',
    CASH: 'Dinheiro',
  }
  return map[method] || method
}

export function formatCPF(cpf: string): string {
  if (!cpf) return '-'
  const digits = cpf.replace(/\D/g, '')
  if (digits.length !== 11) return cpf
  return digits.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
}
