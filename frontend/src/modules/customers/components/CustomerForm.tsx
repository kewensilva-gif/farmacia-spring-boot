import { useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import Dialog from '@mui/material/Dialog'
import DialogTitle from '@mui/material/DialogTitle'
import DialogContent from '@mui/material/DialogContent'
import DialogActions from '@mui/material/DialogActions'
import TextField from '@mui/material/TextField'
import Button from '@mui/material/Button'
import CircularProgress from '@mui/material/CircularProgress'
import Grid from '@mui/material/Grid'
import type { Customer } from '../types/customer.types'

const schema = z.object({
  firstname: z.string().min(1, 'Nome é obrigatório').max(100),
  lastname: z.string().min(1, 'Sobrenome é obrigatório').max(100),
  cpf: z.string().min(11, 'CPF inválido').max(14, 'CPF inválido'),
  registrationDate: z.string().min(1, 'Data de registro é obrigatória'),
  roleName: z.string().min(1),
})

type FormData = z.infer<typeof schema>

interface CustomerFormProps {
  open: boolean
  onClose: () => void
  onSubmit: (data: FormData) => Promise<void>
  customer?: Customer | null
}

export function CustomerForm({ open, onClose, onSubmit, customer }: CustomerFormProps) {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { roleName: 'CUSTOMER' },
  })

  useEffect(() => {
    if (open) {
      reset({
        firstname: customer?.person?.firstname || '',
        lastname: customer?.person?.lastname || '',
        cpf: customer?.person?.cpf || '',
        registrationDate: customer?.registrationDate
          ? customer.registrationDate.split('T')[0]
          : new Date().toISOString().split('T')[0],
        roleName: 'CUSTOMER',
      })
    }
  }, [open, customer, reset])

  const handleFormSubmit = async (data: FormData) => {
    await onSubmit(data)
    reset()
  }

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{customer ? 'Editar Cliente' : 'Novo Cliente'}</DialogTitle>
      <DialogContent>
        <Grid container spacing={2} sx={{ mt: 0.5 }}>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="Nome"
              fullWidth
              {...register('firstname')}
              error={!!errors.firstname}
              helperText={errors.firstname?.message}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="Sobrenome"
              fullWidth
              {...register('lastname')}
              error={!!errors.lastname}
              helperText={errors.lastname?.message}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="CPF"
              fullWidth
              {...register('cpf')}
              error={!!errors.cpf}
              helperText={errors.cpf?.message}
              placeholder="000.000.000-00"
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="Data de Registro"
              type="date"
              fullWidth
              InputLabelProps={{ shrink: true }}
              {...register('registrationDate')}
              error={!!errors.registrationDate}
              helperText={errors.registrationDate?.message}
            />
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onClose} disabled={isSubmitting}>Cancelar</Button>
        <Button variant="contained" onClick={handleSubmit(handleFormSubmit)} disabled={isSubmitting}>
          {isSubmitting ? <CircularProgress size={20} color="inherit" /> : customer ? 'Atualizar' : 'Criar'}
        </Button>
      </DialogActions>
    </Dialog>
  )
}
