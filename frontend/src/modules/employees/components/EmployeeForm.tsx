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
import type { Employee } from '../types/employee.types'

const schema = z.object({
  firstname: z.string().min(1, 'Nome é obrigatório').max(100),
  lastname: z.string().min(1, 'Sobrenome é obrigatório').max(100),
  cpf: z.string().min(11, 'CPF inválido').max(14),
  hiringDate: z.string().min(1, 'Data de contratação é obrigatória'),
  terminationDate: z.string().optional(),
  salary: z.number().min(0, 'Salário inválido'),
  roleName: z.string().min(1),
})

type FormData = z.infer<typeof schema>

interface EmployeeFormProps {
  open: boolean
  onClose: () => void
  onSubmit: (data: FormData) => Promise<void>
  employee?: Employee | null
}

export function EmployeeForm({ open, onClose, onSubmit, employee }: EmployeeFormProps) {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { roleName: 'EMPLOYEE' },
  })

  useEffect(() => {
    if (open) {
      reset({
        firstname: employee?.person?.firstname || '',
        lastname: employee?.person?.lastname || '',
        cpf: employee?.person?.cpf || '',
        hiringDate: employee?.hiringDate ? employee.hiringDate.split('T')[0] : '',
        terminationDate: employee?.terminationDate ? employee.terminationDate.split('T')[0] : '',
        salary: employee?.salary ?? 0,
        roleName: 'EMPLOYEE',
      })
    }
  }, [open, employee, reset])

  const handleFormSubmit = async (data: FormData) => {
    await onSubmit(data)
    reset()
  }

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle>{employee ? 'Editar Funcionário' : 'Novo Funcionário'}</DialogTitle>
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
              label="Salário (R$)"
              type="number"
              fullWidth
              inputProps={{ step: '0.01', min: 0 }}
              {...register('salary', { valueAsNumber: true })}
              error={!!errors.salary}
              helperText={errors.salary?.message}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="Data de Contratação"
              type="date"
              fullWidth
              InputLabelProps={{ shrink: true }}
              {...register('hiringDate')}
              error={!!errors.hiringDate}
              helperText={errors.hiringDate?.message}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="Data de Demissão (opcional)"
              type="date"
              fullWidth
              InputLabelProps={{ shrink: true }}
              {...register('terminationDate')}
              error={!!errors.terminationDate}
              helperText={errors.terminationDate?.message}
            />
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onClose} disabled={isSubmitting}>Cancelar</Button>
        <Button variant="contained" onClick={handleSubmit(handleFormSubmit)} disabled={isSubmitting}>
          {isSubmitting ? <CircularProgress size={20} color="inherit" /> : employee ? 'Atualizar' : 'Criar'}
        </Button>
      </DialogActions>
    </Dialog>
  )
}
