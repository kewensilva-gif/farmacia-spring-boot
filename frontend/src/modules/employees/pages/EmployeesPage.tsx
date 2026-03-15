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
import { EmployeeTable } from '../components/EmployeeTable'
import { EmployeeForm } from '../components/EmployeeForm'
import { employeeService } from '../services/employee.service'
import type { Employee, EmployeeDto } from '../types/employee.types'

interface SnackbarState {
  open: boolean
  message: string
  severity: 'success' | 'error'
}

export function EmployeesPage() {
  const [employees, setEmployees] = useState<Employee[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [formOpen, setFormOpen] = useState(false)
  const [editingEmployee, setEditingEmployee] = useState<Employee | null>(null)
  const [deleteTarget, setDeleteTarget] = useState<Employee | null>(null)
  const [snackbar, setSnackbar] = useState<SnackbarState>({ open: false, message: '', severity: 'success' })

  const fetchEmployees = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await employeeService.getAll()
      setEmployees(data)
    } catch {
      setError('Erro ao carregar funcionários. Tente novamente.')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchEmployees()
  }, [fetchEmployees])

  const handleSubmit = async (data: EmployeeDto) => {
    try {
      if (editingEmployee) {
        await employeeService.update(editingEmployee.id, data)
        setSnackbar({ open: true, message: 'Funcionário atualizado com sucesso!', severity: 'success' })
      } else {
        await employeeService.create(data)
        setSnackbar({ open: true, message: 'Funcionário criado com sucesso!', severity: 'success' })
      }
      setFormOpen(false)
      setEditingEmployee(null)
      await fetchEmployees()
    } catch {
      setSnackbar({ open: true, message: 'Erro ao salvar funcionário.', severity: 'error' })
    }
  }

  const handleDelete = async () => {
    if (!deleteTarget) return
    try {
      await employeeService.delete(deleteTarget.id)
      setDeleteTarget(null)
      await fetchEmployees()
      setSnackbar({ open: true, message: 'Funcionário excluído com sucesso!', severity: 'success' })
    } catch {
      setSnackbar({ open: true, message: 'Erro ao excluir funcionário.', severity: 'error' })
      setDeleteTarget(null)
    }
  }

  return (
    <Box>
      <Box className="flex items-center justify-between mb-6">
        <Box>
          <Typography variant="h5" className="font-bold text-gray-800">
            Funcionários
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Gerencie os funcionários da farmácia
          </Typography>
        </Box>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => { setEditingEmployee(null); setFormOpen(true) }}
        >
          Novo Funcionário
        </Button>
      </Box>

      {error && (
        <Alert severity="error" className="mb-4" action={
          <Button color="inherit" size="small" onClick={fetchEmployees}>
            Tentar novamente
          </Button>
        }>
          {error}
        </Alert>
      )}

      <EmployeeTable
        employees={employees}
        loading={loading}
        onEdit={(e) => { setEditingEmployee(e); setFormOpen(true) }}
        onDelete={setDeleteTarget}
      />

      <EmployeeForm
        open={formOpen}
        onClose={() => { setFormOpen(false); setEditingEmployee(null) }}
        onSubmit={handleSubmit}
        employee={editingEmployee}
      />

      <Dialog open={!!deleteTarget} onClose={() => setDeleteTarget(null)} maxWidth="xs" fullWidth>
        <DialogTitle>Confirmar Exclusão</DialogTitle>
        <DialogContent>
          <Typography>
            Deseja excluir o funcionário{' '}
            <strong>{deleteTarget?.person?.firstname} {deleteTarget?.person?.lastname}</strong>?
            Esta ação não pode ser desfeita.
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
