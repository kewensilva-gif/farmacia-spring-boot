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
import type { Category } from '../types/category.types'

const schema = z.object({
  name: z.string().min(1, 'Nome é obrigatório').max(100, 'Nome muito longo'),
})

type FormData = z.infer<typeof schema>

interface CategoryFormProps {
  open: boolean
  onClose: () => void
  onSubmit: (data: { name: string }) => Promise<void>
  category?: Category | null
}

export function CategoryForm({ open, onClose, onSubmit, category }: CategoryFormProps) {
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
      reset({ name: category?.name || '' })
    }
  }, [open, category, reset])

  const handleFormSubmit = async (data: FormData) => {
    await onSubmit(data)
    reset()
  }

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{category ? 'Editar Categoria' : 'Nova Categoria'}</DialogTitle>
      <DialogContent>
        <TextField
          label="Nome da Categoria"
          fullWidth
          margin="normal"
          {...register('name')}
          error={!!errors.name}
          helperText={errors.name?.message}
          autoFocus
        />
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onClose} disabled={isSubmitting}>
          Cancelar
        </Button>
        <Button
          variant="contained"
          onClick={handleSubmit(handleFormSubmit)}
          disabled={isSubmitting}
        >
          {isSubmitting ? <CircularProgress size={20} color="inherit" /> : category ? 'Atualizar' : 'Criar'}
        </Button>
      </DialogActions>
    </Dialog>
  )
}
