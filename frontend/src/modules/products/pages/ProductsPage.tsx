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
import { ProductTable } from '../components/ProductTable'
import { ProductForm } from '../components/ProductForm'
import { productService } from '../services/product.service'
import type { Product, CreateProductRequest } from '../types/product.types'
import { useAuth } from '../../../contexts/AuthContext'

interface SnackbarState {
  open: boolean
  message: string
  severity: 'success' | 'error'
}

export function ProductsPage() {
  const { user } = useAuth()
  const [products, setProducts] = useState<Product[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [formOpen, setFormOpen] = useState(false)
  const [editingProduct, setEditingProduct] = useState<Product | null>(null)
  const [deleteTarget, setDeleteTarget] = useState<Product | null>(null)
  const [snackbar, setSnackbar] = useState<SnackbarState>({ open: false, message: '', severity: 'success' })

  const isAdmin = user?.role === 'ADMIN'

  const fetchProducts = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await productService.getAll()
      setProducts(data)
    } catch {
      setError('Erro ao carregar produtos. Tente novamente.')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchProducts()
  }, [fetchProducts])

  const handleCreate = async (data: CreateProductRequest) => {
    try {
      await productService.create(data)
      setFormOpen(false)
      await fetchProducts()
      setSnackbar({ open: true, message: 'Produto criado com sucesso!', severity: 'success' })
    } catch {
      setSnackbar({ open: true, message: 'Erro ao criar produto.', severity: 'error' })
    }
  }

  const handleUpdate = async (data: CreateProductRequest) => {
    if (!editingProduct) return
    try {
      await productService.update(editingProduct.id, data)
      setFormOpen(false)
      setEditingProduct(null)
      await fetchProducts()
      setSnackbar({ open: true, message: 'Produto atualizado com sucesso!', severity: 'success' })
    } catch {
      setSnackbar({ open: true, message: 'Erro ao atualizar produto.', severity: 'error' })
    }
  }

  const handleDelete = async () => {
    if (!deleteTarget) return
    try {
      await productService.delete(deleteTarget.id)
      setDeleteTarget(null)
      await fetchProducts()
      setSnackbar({ open: true, message: 'Produto excluído com sucesso!', severity: 'success' })
    } catch {
      setSnackbar({ open: true, message: 'Erro ao excluir produto.', severity: 'error' })
      setDeleteTarget(null)
    }
  }

  return (
    <Box>
      <Box className="flex items-center justify-between mb-6">
        <Box>
          <Typography variant="h5" className="font-bold text-gray-800">
            Produtos
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Gerencie o catálogo de produtos da farmácia
          </Typography>
        </Box>
        {isAdmin && (
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => { setEditingProduct(null); setFormOpen(true) }}
          >
            Novo Produto
          </Button>
        )}
      </Box>

      {error && (
        <Alert severity="error" className="mb-4" action={
          <Button color="inherit" size="small" onClick={fetchProducts}>
            Tentar novamente
          </Button>
        }>
          {error}
        </Alert>
      )}

      <ProductTable
        products={products}
        loading={loading}
        onEdit={(p) => { setEditingProduct(p); setFormOpen(true) }}
        onDelete={setDeleteTarget}
      />

      <ProductForm
        open={formOpen}
        onClose={() => { setFormOpen(false); setEditingProduct(null) }}
        onSubmit={editingProduct ? handleUpdate : handleCreate}
        product={editingProduct}
      />

      <Dialog open={!!deleteTarget} onClose={() => setDeleteTarget(null)} maxWidth="xs" fullWidth>
        <DialogTitle>Confirmar Exclusão</DialogTitle>
        <DialogContent>
          <Typography>
            Deseja excluir o produto <strong>{deleteTarget?.name}</strong>? Esta ação não pode ser desfeita.
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
