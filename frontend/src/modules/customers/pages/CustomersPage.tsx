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
import { CustomerTable } from '../components/CustomerTable'
import { CustomerForm } from '../components/CustomerForm'
import { customerService } from '../services/customer.service'
import type { Customer, CustomerDto } from '../types/customer.types'
import { useAuth } from '../../../contexts/AuthContext'

interface SnackbarState {
  open: boolean
  message: string
  severity: 'success' | 'error'
}

export function CustomersPage() {
  const { user } = useAuth()
  const [customers, setCustomers] = useState<Customer[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [formOpen, setFormOpen] = useState(false)
  const [editingCustomer, setEditingCustomer] = useState<Customer | null>(null)
  const [deleteTarget, setDeleteTarget] = useState<Customer | null>(null)
  const [snackbar, setSnackbar] = useState<SnackbarState>({ open: false, message: '', severity: 'success' })

  const isAdmin = user?.role === 'ADMIN'
  const canWrite = isAdmin || user?.role === 'EMPLOYEE'

  const fetchCustomers = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await customerService.getAll()
      setCustomers(data)
    } catch {
      setError('Erro ao carregar clientes. Tente novamente.')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchCustomers()
  }, [fetchCustomers])

  const handleSubmit = async (data: CustomerDto) => {
    try {
      if (editingCustomer) {
        await customerService.update(editingCustomer.id, data)
        setSnackbar({ open: true, message: 'Cliente atualizado com sucesso!', severity: 'success' })
      } else {
        await customerService.create(data)
        setSnackbar({ open: true, message: 'Cliente criado com sucesso!', severity: 'success' })
      }
      setFormOpen(false)
      setEditingCustomer(null)
      await fetchCustomers()
    } catch {
      setSnackbar({ open: true, message: 'Erro ao salvar cliente.', severity: 'error' })
    }
  }

  const handleDelete = async () => {
    if (!deleteTarget) return
    try {
      await customerService.delete(deleteTarget.id)
      setDeleteTarget(null)
      await fetchCustomers()
      setSnackbar({ open: true, message: 'Cliente excluído com sucesso!', severity: 'success' })
    } catch {
      setSnackbar({ open: true, message: 'Erro ao excluir cliente.', severity: 'error' })
      setDeleteTarget(null)
    }
  }

  return (
    <Box>
      <Box className="flex items-center justify-between mb-6">
        <Box>
          <Typography variant="h5" className="font-bold text-gray-800">
            Clientes
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Gerencie os clientes da farmácia
          </Typography>
        </Box>
        {canWrite && (
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => { setEditingCustomer(null); setFormOpen(true) }}
          >
            Novo Cliente
          </Button>
        )}
      </Box>

      {error && (
        <Alert severity="error" className="mb-4" action={
          <Button color="inherit" size="small" onClick={fetchCustomers}>
            Tentar novamente
          </Button>
        }>
          {error}
        </Alert>
      )}

      <CustomerTable
        customers={customers}
        loading={loading}
        onEdit={(c) => { setEditingCustomer(c); setFormOpen(true) }}
        onDelete={setDeleteTarget}
      />

      <CustomerForm
        open={formOpen}
        onClose={() => { setFormOpen(false); setEditingCustomer(null) }}
        onSubmit={handleSubmit}
        customer={editingCustomer}
      />

      <Dialog open={!!deleteTarget} onClose={() => setDeleteTarget(null)} maxWidth="xs" fullWidth>
        <DialogTitle>Confirmar Exclusão</DialogTitle>
        <DialogContent>
          <Typography>
            Deseja excluir o cliente{' '}
            <strong>{deleteTarget?.person?.firstname} {deleteTarget?.person?.lastname}</strong>?
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
