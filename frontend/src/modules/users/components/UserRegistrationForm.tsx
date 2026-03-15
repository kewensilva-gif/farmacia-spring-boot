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
import Typography from '@mui/material/Typography'
import Divider from '@mui/material/Divider'
import type { UserRegistrationRequest } from '../types/user.types'
import type { Role } from '../../roles/types/role.types'
import { roleService } from '../../roles/services/role.service'

const schema = z.object({
  firstName: z.string().min(1, 'Nome é obrigatório').max(100),
  lastName: z.string().min(1, 'Sobrenome é obrigatório').max(100),
  cpf: z.string().min(11, 'CPF inválido').max(14),
  username: z.string().min(3, 'Usuário deve ter pelo menos 3 caracteres').max(50),
  email: z.string().min(1, 'E-mail é obrigatório').email('E-mail inválido'),
  password: z.string().min(6, 'Senha deve ter pelo menos 6 caracteres'),
  roleName: z.string().min(1, 'Perfil é obrigatório'),
  registrationDate: z.string().optional(),
  hiringDate: z.string().optional(),
  salary: z.number().optional(),
})

type FormData = z.infer<typeof schema>

interface UserRegistrationFormProps {
  open: boolean
  onClose: () => void
  onSubmit: (data: UserRegistrationRequest) => Promise<void>
}

export function UserRegistrationForm({ open, onClose, onSubmit }: UserRegistrationFormProps) {
  const [roles, setRoles] = useState<Role[]>([])

  const {
    register,
    handleSubmit,
    control,
    watch,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { roleName: 'CUSTOMER' },
  })

  const selectedRole = watch('roleName')

  useEffect(() => {
    roleService.getAll().then(setRoles).catch(() => {})
  }, [])

  useEffect(() => {
    if (open) {
      reset({
        firstName: '',
        lastName: '',
        cpf: '',
        username: '',
        email: '',
        password: '',
        roleName: 'CUSTOMER',
        registrationDate: new Date().toISOString().split('T')[0],
        hiringDate: '',
        salary: undefined,
      })
    }
  }, [open, reset])

  const handleFormSubmit = async (data: FormData) => {
    const payload: UserRegistrationRequest = {
      firstName: data.firstName,
      lastName: data.lastName,
      cpf: data.cpf,
      username: data.username,
      email: data.email,
      password: data.password,
      roleName: data.roleName,
    }

    if (data.roleName === 'CUSTOMER' && data.registrationDate) {
      payload.registrationDate = data.registrationDate
    }

    if (data.roleName === 'EMPLOYEE') {
      if (data.hiringDate) payload.hiringDate = data.hiringDate
      if (data.salary !== undefined) payload.salary = data.salary
    }

    await onSubmit(payload)
    reset()
  }

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle>Registrar Novo Usuário</DialogTitle>
      <DialogContent>
        <Grid container spacing={2} sx={{ mt: 0.5 }}>
          <Grid size={{ xs: 12 }}>
            <Typography variant="subtitle2" color="text.secondary" className="mb-1">
              Informações Pessoais
            </Typography>
            <Divider className="mb-2" />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="Nome"
              fullWidth
              {...register('firstName')}
              error={!!errors.firstName}
              helperText={errors.firstName?.message}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="Sobrenome"
              fullWidth
              {...register('lastName')}
              error={!!errors.lastName}
              helperText={errors.lastName?.message}
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
          <Grid size={{ xs: 12 }}>
            <Typography variant="subtitle2" color="text.secondary" className="mt-2 mb-1">
              Dados de Acesso
            </Typography>
            <Divider className="mb-2" />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="Nome de Usuário"
              fullWidth
              {...register('username')}
              error={!!errors.username}
              helperText={errors.username?.message}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="E-mail"
              type="email"
              fullWidth
              {...register('email')}
              error={!!errors.email}
              helperText={errors.email?.message}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="Senha"
              type="password"
              fullWidth
              {...register('password')}
              error={!!errors.password}
              helperText={errors.password?.message}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="roleName"
              control={control}
              render={({ field }) => (
                <TextField
                  select
                  label="Perfil de Acesso"
                  fullWidth
                  {...field}
                  error={!!errors.roleName}
                  helperText={errors.roleName?.message}
                >
                  {roles.length > 0
                    ? roles.map((role) => (
                        <MenuItem key={role.uuid} value={role.name}>{role.name}</MenuItem>
                      ))
                    : ['ADMIN', 'EMPLOYEE', 'CUSTOMER'].map((r) => (
                        <MenuItem key={r} value={r}>{r}</MenuItem>
                      ))}
                </TextField>
              )}
            />
          </Grid>

          {selectedRole === 'CUSTOMER' && (
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
          )}

          {selectedRole === 'EMPLOYEE' && (
            <>
              <Grid size={{ xs: 12 }}>
                <Typography variant="subtitle2" color="text.secondary" className="mt-2 mb-1">
                  Dados do Funcionário
                </Typography>
                <Divider className="mb-2" />
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
                  label="Salário (R$)"
                  type="number"
                  fullWidth
                  inputProps={{ step: '0.01', min: 0 }}
                  {...register('salary', { valueAsNumber: true })}
                  error={!!errors.salary}
                  helperText={errors.salary?.message}
                />
              </Grid>
            </>
          )}
        </Grid>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onClose} disabled={isSubmitting}>Cancelar</Button>
        <Button variant="contained" onClick={handleSubmit(handleFormSubmit)} disabled={isSubmitting}>
          {isSubmitting ? <CircularProgress size={20} color="inherit" /> : 'Registrar'}
        </Button>
      </DialogActions>
    </Dialog>
  )
}
