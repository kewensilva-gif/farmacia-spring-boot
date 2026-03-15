import { useEffect, useState } from 'react'
import { useForm, Controller } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import Dialog from '@mui/material/Dialog'
import DialogTitle from '@mui/material/DialogTitle'
import DialogContent from '@mui/material/DialogContent'
import DialogActions from '@mui/material/DialogActions'
import TextField from '@mui/material/TextField'
import Button from '@mui/material/Button'
import CircularProgress from '@mui/material/CircularProgress'
import MenuItem from '@mui/material/MenuItem'
import Grid from '@mui/material/Grid'
import type { Sale } from '../types/sale.types'
import type { Employee } from '../../employees/types/employee.types'
import type { Customer } from '../../customers/types/customer.types'
import { employeeService } from '../../employees/services/employee.service'
import { customerService } from '../../customers/services/customer.service'

const PAYMENT_METHODS = [
  { value: 'CREDITCARD', label: 'Cartão de Crédito' },
  { value: 'DEBITCARD', label: 'Cartão de Débito' },
  { value: 'PIX', label: 'Pix' },
  { value: 'CASH', label: 'Dinheiro' },
]

const schema = z.object({
  totalPrice: z.number().min(0, 'Valor total inválido'),
  discount: z.number().min(0, 'Desconto inválido'),
  paymentMethod: z.enum(['CREDITCARD', 'DEBITCARD', 'PIX', 'CASH']),
  employeeId: z.number().min(1, 'Selecione um funcionário'),
  customerId: z.number().optional(),
})

type FormData = z.infer<typeof schema>

interface SaleFormProps {
  open: boolean
  onClose: () => void
  onSubmit: (data: {
    totalPrice: number
    discount: number
    paymentMethod: 'CREDITCARD' | 'DEBITCARD' | 'PIX' | 'CASH'
    employee: { id: number }
    customer?: { id: number }
  }) => Promise<void>
  sale?: Sale | null
}

export function SaleForm({ open, onClose, onSubmit, sale }: SaleFormProps) {
  const [employees, setEmployees] = useState<Employee[]>([])
  const [customers, setCustomers] = useState<Customer[]>([])

  const {
    register,
    handleSubmit,
    control,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
  })

  useEffect(() => {
    employeeService.getAll().then(setEmployees).catch(() => {})
    customerService.getAll().then(setCustomers).catch(() => {})
  }, [])

  useEffect(() => {
    if (open) {
      reset({
        totalPrice: sale?.totalPrice ?? 0,
        discount: sale?.discount ?? 0,
        paymentMethod: sale?.paymentMethod ?? 'CASH',
        employeeId: sale?.employee?.id ?? 0,
        customerId: sale?.customer?.id ?? undefined,
      })
    }
  }, [open, sale, reset])

  const handleFormSubmit = async (data: FormData) => {
    await onSubmit({
      totalPrice: data.totalPrice,
      discount: data.discount,
      paymentMethod: data.paymentMethod,
      employee: { id: data.employeeId },
      customer: data.customerId ? { id: data.customerId } : undefined,
    })
    reset()
  }

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle>{sale ? 'Editar Venda' : 'Nova Venda'}</DialogTitle>
      <DialogContent>
        <Grid container spacing={2} sx={{ mt: 0.5 }}>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="Valor Total (R$)"
              type="number"
              fullWidth
              inputProps={{ step: '0.01', min: 0 }}
              {...register('totalPrice', { valueAsNumber: true })}
              error={!!errors.totalPrice}
              helperText={errors.totalPrice?.message}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="Desconto (R$)"
              type="number"
              fullWidth
              inputProps={{ step: '0.01', min: 0 }}
              {...register('discount', { valueAsNumber: true })}
              error={!!errors.discount}
              helperText={errors.discount?.message}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="paymentMethod"
              control={control}
              render={({ field }) => (
                <TextField
                  select
                  label="Forma de Pagamento"
                  fullWidth
                  {...field}
                  error={!!errors.paymentMethod}
                  helperText={errors.paymentMethod?.message}
                >
                  {PAYMENT_METHODS.map((pm) => (
                    <MenuItem key={pm.value} value={pm.value}>{pm.label}</MenuItem>
                  ))}
                </TextField>
              )}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="employeeId"
              control={control}
              render={({ field }) => (
                <TextField
                  select
                  label="Funcionário"
                  fullWidth
                  {...field}
                  value={field.value || ''}
                  onChange={(e) => field.onChange(Number(e.target.value))}
                  error={!!errors.employeeId}
                  helperText={errors.employeeId?.message}
                >
                  <MenuItem value={0} disabled>Selecione um funcionário</MenuItem>
                  {employees.map((emp) => (
                    <MenuItem key={emp.id} value={emp.id}>
                      {emp.person?.firstname} {emp.person?.lastname}
                    </MenuItem>
                  ))}
                </TextField>
              )}
            />
          </Grid>
          <Grid size={{ xs: 12 }}>
            <Controller
              name="customerId"
              control={control}
              render={({ field }) => (
                <TextField
                  select
                  label="Cliente (opcional)"
                  fullWidth
                  {...field}
                  value={field.value || ''}
                  onChange={(e) => field.onChange(e.target.value ? Number(e.target.value) : undefined)}
                  error={!!errors.customerId}
                  helperText={errors.customerId?.message}
                >
                  <MenuItem value="">Nenhum cliente</MenuItem>
                  {customers.map((cust) => (
                    <MenuItem key={cust.id} value={cust.id}>
                      {cust.person?.firstname} {cust.person?.lastname}
                    </MenuItem>
                  ))}
                </TextField>
              )}
            />
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onClose} disabled={isSubmitting}>Cancelar</Button>
        <Button variant="contained" onClick={handleSubmit(handleFormSubmit)} disabled={isSubmitting}>
          {isSubmitting ? <CircularProgress size={20} color="inherit" /> : sale ? 'Atualizar' : 'Criar'}
        </Button>
      </DialogActions>
    </Dialog>
  )
}
