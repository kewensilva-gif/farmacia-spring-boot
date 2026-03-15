import { useState, useEffect, useCallback } from 'react'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Typography from '@mui/material/Typography'
import Alert from '@mui/material/Alert'
import Snackbar from '@mui/material/Snackbar'
import Dialog from '@mui/material/Dialog'
import DialogTitle from '@mui/material/DialogTitle'
import DialogContent from '@mui/material/DialogContent'
import DialogActions from '@mui/material/DialogActions'
import AddIcon from '@mui/icons-material/Add'
import { CategoryTable } from '../components/CategoryTable'
import { CategoryForm } from '../components/CategoryForm'
import { categoryService } from '../services/category.service'
import type { Category } from '../types/category.types'
import { useAuth } from '../../../contexts/AuthContext'

interface SnackbarState {
  open: boolean
  message: string
  severity: 'success' | 'error'
}

export function CategoriesPage() {
  const { user } = useAuth()
  const [categories, setCategories] = useState<Category[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [formOpen, setFormOpen] = useState(false)
  const [editingCategory, setEditingCategory] = useState<Category | null>(null)
  const [deleteTarget, setDeleteTarget] = useState<Category | null>(null)
  const [snackbar, setSnackbar] = useState<SnackbarState>({ open: false, message: '', severity: 'success' })

  const isAdmin = user?.role === 'ADMIN'

  const fetchCategories = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await categoryService.getAll()
      setCategories(data)
    } catch {
      setError('Erro ao carregar categorias. Tente novamente.')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchCategories()
  }, [fetchCategories])

  const handleCreate = async (data: { name: string }) => {
    try {
      await categoryService.create(data)
      setFormOpen(false)
      await fetchCategories()
      setSnackbar({ open: true, message: 'Categoria criada com sucesso!', severity: 'success' })
    } catch {
      setSnackbar({ open: true, message: 'Erro ao criar categoria.', severity: 'error' })
    }
  }

  const handleUpdate = async (data: { name: string }) => {
    if (!editingCategory) return
    try {
      await categoryService.update(editingCategory.id, data)
      setFormOpen(false)
      setEditingCategory(null)
      await fetchCategories()
      setSnackbar({ open: true, message: 'Categoria atualizada com sucesso!', severity: 'success' })
    } catch {
      setSnackbar({ open: true, message: 'Erro ao atualizar categoria.', severity: 'error' })
    }
  }

  const handleDelete = async () => {
    if (!deleteTarget) return
    try {
      await categoryService.delete(deleteTarget.id)
      setDeleteTarget(null)
      await fetchCategories()
      setSnackbar({ open: true, message: 'Categoria excluída com sucesso!', severity: 'success' })
    } catch {
      setSnackbar({ open: true, message: 'Erro ao excluir categoria.', severity: 'error' })
      setDeleteTarget(null)
    }
  }

  return (
    <Box>
      <Box className="flex items-center justify-between mb-6">
        <Box>
          <Typography variant="h5" className="font-bold text-gray-800">
            Categorias
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Gerencie as categorias de produtos
          </Typography>
        </Box>
        {isAdmin && (
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => { setEditingCategory(null); setFormOpen(true) }}
          >
            Nova Categoria
          </Button>
        )}
      </Box>

      {error && (
        <Alert severity="error" className="mb-4" action={
          <Button color="inherit" size="small" onClick={fetchCategories}>
            Tentar novamente
          </Button>
        }>
          {error}
        </Alert>
      )}

      <CategoryTable
        categories={categories}
        loading={loading}
        onEdit={(c) => { setEditingCategory(c); setFormOpen(true) }}
        onDelete={setDeleteTarget}
      />

      <CategoryForm
        open={formOpen}
        onClose={() => { setFormOpen(false); setEditingCategory(null) }}
        onSubmit={editingCategory ? handleUpdate : handleCreate}
        category={editingCategory}
      />

      <Dialog open={!!deleteTarget} onClose={() => setDeleteTarget(null)} maxWidth="xs" fullWidth>
        <DialogTitle>Confirmar Exclusão</DialogTitle>
        <DialogContent>
          <Typography>
            Deseja excluir a categoria <strong>{deleteTarget?.name}</strong>? Esta ação não pode ser desfeita.
          </Typography>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setDeleteTarget(null)}>Cancelar</Button>
          <Button variant="contained" color="error" onClick={handleDelete}>
            Excluir
          </Button>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={() => setSnackbar((s) => ({ ...s, open: false }))}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        <Alert severity={snackbar.severity} onClose={() => setSnackbar((s) => ({ ...s, open: false }))}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  )
}
