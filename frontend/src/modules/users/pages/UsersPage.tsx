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
import PersonAddIcon from '@mui/icons-material/PersonAdd'
import { UserTable } from '../components/UserTable'
import { UserRegistrationForm } from '../components/UserRegistrationForm'
import { userService } from '../services/user.service'
import type { User, UserRegistrationRequest } from '../types/user.types'

interface SnackbarState {
  open: boolean
  message: string
  severity: 'success' | 'error'
}

export function UsersPage() {
  const [users, setUsers] = useState<User[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [registrationOpen, setRegistrationOpen] = useState(false)
  const [deleteTarget, setDeleteTarget] = useState<User | null>(null)
  const [snackbar, setSnackbar] = useState<SnackbarState>({ open: false, message: '', severity: 'success' })

  const fetchUsers = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await userService.getAll()
      setUsers(data)
    } catch {
      setError('Erro ao carregar usuários. Tente novamente.')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchUsers()
  }, [fetchUsers])

  const handleRegister = async (data: UserRegistrationRequest) => {
    try {
      await userService.adminRegister(data)
      setRegistrationOpen(false)
      await fetchUsers()
      setSnackbar({ open: true, message: 'Usuário registrado com sucesso!', severity: 'success' })
    } catch {
      setSnackbar({ open: true, message: 'Erro ao registrar usuário.', severity: 'error' })
    }
  }

  const handleToggleEnabled = async (user: User, enabled: boolean) => {
    try {
      await userService.update(user.uuid, {
        username: user.username,
        email: user.email,
        enabled,
        roleName: user.role?.name || 'CUSTOMER',
      })
      setUsers((prev) => prev.map((u) => (u.uuid === user.uuid ? { ...u, enabled } : u)))
      setSnackbar({ open: true, message: `Usuário ${enabled ? 'ativado' : 'desativado'} com sucesso!`, severity: 'success' })
    } catch {
      setSnackbar({ open: true, message: 'Erro ao atualizar usuário.', severity: 'error' })
    }
  }

  const handleDelete = async () => {
    if (!deleteTarget) return
    try {
      await userService.delete(deleteTarget.uuid)
      setDeleteTarget(null)
      await fetchUsers()
      setSnackbar({ open: true, message: 'Usuário excluído com sucesso!', severity: 'success' })
    } catch {
      setSnackbar({ open: true, message: 'Erro ao excluir usuário.', severity: 'error' })
      setDeleteTarget(null)
    }
  }

  return (
    <Box>
      <Box className="flex items-center justify-between mb-6">
        <Box>
          <Typography variant="h5" className="font-bold text-gray-800">
            Usuários
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Gerencie as contas de usuários do sistema
          </Typography>
        </Box>
        <Button
          variant="contained"
          startIcon={<PersonAddIcon />}
          onClick={() => setRegistrationOpen(true)}
        >
          Novo Usuário
        </Button>
      </Box>

      {error && (
        <Alert severity="error" className="mb-4" action={
          <Button color="inherit" size="small" onClick={fetchUsers}>
            Tentar novamente
          </Button>
        }>
          {error}
        </Alert>
      )}

      <UserTable
        users={users}
        loading={loading}
        onEdit={() => {}}
        onDelete={setDeleteTarget}
        onToggleEnabled={handleToggleEnabled}
      />

      <UserRegistrationForm
        open={registrationOpen}
        onClose={() => setRegistrationOpen(false)}
        onSubmit={handleRegister}
      />

      <Dialog open={!!deleteTarget} onClose={() => setDeleteTarget(null)} maxWidth="xs" fullWidth>
        <DialogTitle>Confirmar Exclusão</DialogTitle>
        <DialogContent>
          <Typography>
            Deseja excluir o usuário <strong>{deleteTarget?.username}</strong>?
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
