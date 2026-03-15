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
import type { Role } from '../types/role.types'

const schema = z.object({
  name: z.string().min(1, 'Nome é obrigatório').max(50, 'Nome muito longo').toUpperCase(),
})

type FormData = z.infer<typeof schema>

interface RoleFormProps {
  open: boolean
  onClose: () => void
  onSubmit: (data: { name: string }) => Promise<void>
  role?: Role | null
}

export function RoleForm({ open, onClose, onSubmit, role }: RoleFormProps) {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
  })

  useEffect(() => {
    if (open) {
      reset({ name: role?.name || '' })
    }
  }, [open, role, reset])

  const handleFormSubmit = async (data: FormData) => {
    await onSubmit({ name: data.name.toUpperCase() })
    reset()
  }

  return (
    <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle>{role ? 'Editar Perfil' : 'Novo Perfil de Acesso'}</DialogTitle>
      <DialogContent>
        <TextField
          label="Nome do Perfil"
          fullWidth
          margin="normal"
          placeholder="Ex: ADMIN, EMPLOYEE, CUSTOMER"
          {...register('name')}
          error={!!errors.name}
          helperText={errors.name?.message}
          autoFocus
          inputProps={{ style: { textTransform: 'uppercase' } }}
        />
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onClose} disabled={isSubmitting}>Cancelar</Button>
        <Button variant="contained" onClick={handleSubmit(handleFormSubmit)} disabled={isSubmitting}>
          {isSubmitting ? <CircularProgress size={20} color="inherit" /> : role ? 'Atualizar' : 'Criar'}
        </Button>
      </DialogActions>
    </Dialog>
  )
}
