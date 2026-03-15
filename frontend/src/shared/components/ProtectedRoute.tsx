import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../../contexts/AuthContext'
import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'
import Button from '@mui/material/Button'
import CircularProgress from '@mui/material/CircularProgress'

interface ProtectedRouteProps {
  allowedRoles?: string[]
}

export function ProtectedRoute({ allowedRoles }: ProtectedRouteProps) {
  const { user, isLoading } = useAuth()

  if (isLoading) {
    return (
      <Box className="flex items-center justify-center min-h-screen">
        <CircularProgress size={48} />
      </Box>
    )
  }

  if (!user) {
    return <Navigate to="/login" replace />
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return (
      <Box className="flex flex-col items-center justify-center min-h-screen gap-4">
        <Typography variant="h4" color="error">
          403 - Acesso Negado
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Você não tem permissão para acessar esta página.
        </Typography>
        <Button variant="contained" href="/dashboard">
          Voltar ao Dashboard
        </Button>
      </Box>
    )
  }

  return <Outlet />
}
