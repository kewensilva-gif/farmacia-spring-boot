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
import { RoleTable } from '../components/RoleTable'
import { RoleForm } from '../components/RoleForm'
import { roleService } from '../services/role.service'
import type { Role } from '../types/role.types'

interface SnackbarState {
  open: boolean
  message: string
  severity: 'success' | 'error'
}

export function RolesPage() {
  const [roles, setRoles] = useState<Role[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [formOpen, setFormOpen] = useState(false)
  const [editingRole, setEditingRole] = useState<Role | null>(null)
  const [deleteTarget, setDeleteTarget] = useState<Role | null>(null)
  const [snackbar, setSnackbar] = useState<SnackbarState>({ open: false, message: '', severity: 'success' })

  const fetchRoles = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await roleService.getAll()
      setRoles(data)
    } catch {
      setError('Erro ao carregar perfis. Tente novamente.')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchRoles()
  }, [fetchRoles])

  const handleSubmit = async (data: { name: string }) => {
    try {
      if (editingRole) {
        await roleService.update(editingRole.uuid, data)
        setSnackbar({ open: true, message: 'Perfil atualizado com sucesso!', severity: 'success' })
      } else {
        await roleService.create(data)
        setSnackbar({ open: true, message: 'Perfil criado com sucesso!', severity: 'success' })
      }
      setFormOpen(false)
      setEditingRole(null)
      await fetchRoles()
    } catch {
      setSnackbar({ open: true, message: 'Erro ao salvar perfil.', severity: 'error' })
    }
  }

  const handleDelete = async () => {
    if (!deleteTarget) return
    try {
      await roleService.delete(deleteTarget.uuid)
      setDeleteTarget(null)
      await fetchRoles()
      setSnackbar({ open: true, message: 'Perfil excluído com sucesso!', severity: 'success' })
    } catch {
      setSnackbar({ open: true, message: 'Erro ao excluir perfil.', severity: 'error' })
      setDeleteTarget(null)
    }
  }

  return (
    <Box>
      <Box className="flex items-center justify-between mb-6">
        <Box>
          <Typography variant="h5" className="font-bold text-gray-800">
            Perfis de Acesso
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Gerencie os perfis de acesso do sistema
          </Typography>
        </Box>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => { setEditingRole(null); setFormOpen(true) }}
        >
          Novo Perfil
        </Button>
      </Box>

      {error && (
        <Alert severity="error" className="mb-4" action={
          <Button color="inherit" size="small" onClick={fetchRoles}>
            Tentar novamente
          </Button>
        }>
          {error}
        </Alert>
      )}

      <RoleTable
        roles={roles}
        loading={loading}
        onEdit={(r) => { setEditingRole(r); setFormOpen(true) }}
        onDelete={setDeleteTarget}
      />

      <RoleForm
        open={formOpen}
        onClose={() => { setFormOpen(false); setEditingRole(null) }}
        onSubmit={handleSubmit}
        role={editingRole}
      />

      <Dialog open={!!deleteTarget} onClose={() => setDeleteTarget(null)} maxWidth="xs" fullWidth>
        <DialogTitle>Confirmar Exclusão</DialogTitle>
        <DialogContent>
          <Typography>
            Deseja excluir o perfil <strong>{deleteTarget?.name}</strong>?
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
