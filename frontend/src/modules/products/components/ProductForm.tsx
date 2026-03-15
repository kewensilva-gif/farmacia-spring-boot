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
import type { Product } from '../types/product.types'
import type { Category } from '../../categories/types/category.types'
import { categoryService } from '../../categories/services/category.service'

const schema = z.object({
  name: z.string().min(1, 'Nome é obrigatório').max(200, 'Nome muito longo'),
  unitPrice: z.number().min(0, 'Preço inválido'),
  barcode: z.string().min(1, 'Código de barras é obrigatório'),
  stockQuantity: z.number().int().min(0, 'Quantidade inválida'),
  expirationDate: z.string().min(1, 'Data de validade é obrigatória'),
  categoryId: z.number().min(1, 'Selecione uma categoria'),
})

type FormData = z.infer<typeof schema>

interface ProductFormProps {
  open: boolean
  onClose: () => void
  onSubmit: (data: { name: string; unitPrice: number; barcode: string; stockQuantity: number; expirationDate: string; category: { id: number } }) => Promise<void>
  product?: Product | null
}

export function ProductForm({ open, onClose, onSubmit, product }: ProductFormProps) {
  const [categories, setCategories] = useState<Category[]>([])

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
    categoryService.getAll().then(setCategories).catch(() => {})
  }, [])

  useEffect(() => {
    if (open) {
      reset({
        name: product?.name || '',
        unitPrice: product?.unitPrice ?? 0,
        barcode: product?.barcode || '',
        stockQuantity: product?.stockQuantity ?? 0,
        expirationDate: product?.expirationDate ? product.expirationDate.split('T')[0] : '',
        categoryId: product?.category?.id ?? 0,
      })
    }
  }, [open, product, reset])

  const handleFormSubmit = async (data: FormData) => {
    await onSubmit({
      name: data.name,
      unitPrice: data.unitPrice,
      barcode: data.barcode,
      stockQuantity: data.stockQuantity,
      expirationDate: data.expirationDate,
      category: { id: data.categoryId },
    })
    reset()
  }

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle>{product ? 'Editar Produto' : 'Novo Produto'}</DialogTitle>
      <DialogContent>
        <Grid container spacing={2} sx={{ mt: 0.5 }}>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="Nome do Produto"
              fullWidth
              {...register('name')}
              error={!!errors.name}
              helperText={errors.name?.message}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="Código de Barras"
              fullWidth
              {...register('barcode')}
              error={!!errors.barcode}
              helperText={errors.barcode?.message}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="Preço Unitário (R$)"
              type="number"
              fullWidth
              inputProps={{ step: '0.01', min: 0 }}
              {...register('unitPrice', { valueAsNumber: true })}
              error={!!errors.unitPrice}
              helperText={errors.unitPrice?.message}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="Estoque"
              type="number"
              fullWidth
              inputProps={{ min: 0 }}
              {...register('stockQuantity', { valueAsNumber: true })}
              error={!!errors.stockQuantity}
              helperText={errors.stockQuantity?.message}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="Data de Validade"
              type="date"
              fullWidth
              InputLabelProps={{ shrink: true }}
              {...register('expirationDate')}
              error={!!errors.expirationDate}
              helperText={errors.expirationDate?.message}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="categoryId"
              control={control}
              render={({ field }) => (
                <TextField
                  select
                  label="Categoria"
                  fullWidth
                  {...field}
                  value={field.value || ''}
                  onChange={(e) => field.onChange(Number(e.target.value))}
                  error={!!errors.categoryId}
                  helperText={errors.categoryId?.message}
                >
                  <MenuItem value={0} disabled>Selecione uma categoria</MenuItem>
                  {categories.map((cat) => (
                    <MenuItem key={cat.id} value={cat.id}>{cat.name}</MenuItem>
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
          {isSubmitting ? <CircularProgress size={20} color="inherit" /> : product ? 'Atualizar' : 'Criar'}
        </Button>
      </DialogActions>
    </Dialog>
  )
}
