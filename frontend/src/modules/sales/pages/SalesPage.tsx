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
import { SaleTable } from '../components/SaleTable'
import { SaleForm } from '../components/SaleForm'
import { saleService } from '../services/sale.service'
import type { Sale, CreateSaleRequest } from '../types/sale.types'
import { useAuth } from '../../../contexts/AuthContext'

interface SnackbarState {
  open: boolean
  message: string
  severity: 'success' | 'error'
}

export function SalesPage() {
  const { user } = useAuth()
  const [sales, setSales] = useState<Sale[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [formOpen, setFormOpen] = useState(false)
  const [editingSale, setEditingSale] = useState<Sale | null>(null)
  const [deleteTarget, setDeleteTarget] = useState<Sale | null>(null)
  const [snackbar, setSnackbar] = useState<SnackbarState>({ open: false, message: '', severity: 'success' })

  const isAdmin = user?.role === 'ADMIN'

  const fetchSales = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await saleService.getAll()
      setSales(data)
    } catch {
      setError('Erro ao carregar vendas. Tente novamente.')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchSales()
  }, [fetchSales])

  const handleSubmit = async (data: CreateSaleRequest) => {
    try {
      if (editingSale) {
        await saleService.update(editingSale.id, data)
        setSnackbar({ open: true, message: 'Venda atualizada com sucesso!', severity: 'success' })
      } else {
        await saleService.create(data)
        setSnackbar({ open: true, message: 'Venda criada com sucesso!', severity: 'success' })
      }
      setFormOpen(false)
      setEditingSale(null)
      await fetchSales()
    } catch {
      setSnackbar({ open: true, message: 'Erro ao salvar venda.', severity: 'error' })
    }
  }

  const handleDelete = async () => {
    if (!deleteTarget) return
    try {
      await saleService.delete(deleteTarget.id)
      setDeleteTarget(null)
      await fetchSales()
      setSnackbar({ open: true, message: 'Venda excluída com sucesso!', severity: 'success' })
    } catch {
      setSnackbar({ open: true, message: 'Erro ao excluir venda.', severity: 'error' })
      setDeleteTarget(null)
    }
  }

  return (
    <Box>
      <Box className="flex items-center justify-between mb-6">
        <Box>
          <Typography variant="h5" className="font-bold text-gray-800">
            Vendas
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Gerencie as vendas da farmácia
          </Typography>
        </Box>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => { setEditingSale(null); setFormOpen(true) }}
        >
          Nova Venda
        </Button>
      </Box>

      {error && (
        <Alert severity="error" className="mb-4" action={
          <Button color="inherit" size="small" onClick={fetchSales}>
            Tentar novamente
          </Button>
        }>
          {error}
        </Alert>
      )}

      <SaleTable
        sales={sales}
        loading={loading}
        onEdit={(s) => { setEditingSale(s); setFormOpen(true) }}
        onDelete={setDeleteTarget}
      />

      <SaleForm
        open={formOpen}
        onClose={() => { setFormOpen(false); setEditingSale(null) }}
        onSubmit={handleSubmit}
        sale={editingSale}
      />

      <Dialog open={!!deleteTarget} onClose={() => setDeleteTarget(null)} maxWidth="xs" fullWidth>
        <DialogTitle>Confirmar Exclusão</DialogTitle>
        <DialogContent>
          <Typography>
            Deseja excluir a venda <strong>#{deleteTarget?.id}</strong>?
            Esta ação não pode ser desfeita.
          </Typography>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setDeleteTarget(null)}>Cancelar</Button>
          {isAdmin && (
            <Button variant="contained" color="error" onClick={handleDelete}>
              Excluir
            </Button>
          )}
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
