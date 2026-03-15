import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import Box from '@mui/material/Box'
import Card from '@mui/material/Card'
import CardContent from '@mui/material/CardContent'
import TextField from '@mui/material/TextField'
import Button from '@mui/material/Button'
import Typography from '@mui/material/Typography'
import Alert from '@mui/material/Alert'
import CircularProgress from '@mui/material/CircularProgress'
import LocalPharmacyIcon from '@mui/icons-material/LocalPharmacy'
import InputAdornment from '@mui/material/InputAdornment'
import LockIcon from '@mui/icons-material/Lock'
import PersonIcon from '@mui/icons-material/Person'
import { useAuth } from '../../../contexts/AuthContext'

const loginSchema = z.object({
  login: z.string().min(1, 'Usuário ou e-mail é obrigatório'),
  password: z.string().min(1, 'Senha é obrigatória'),
})

type LoginFormData = z.infer<typeof loginSchema>

export function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [error, setError] = useState<string | null>(null)

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  })

  const onSubmit = async (data: LoginFormData) => {
    setError(null)
    try {
      await login(data.login, data.password)
      navigate('/dashboard')
    } catch (err: unknown) {
      const e = err as { response?: { data?: { message?: string } } }
      setError(e?.response?.data?.message || 'Credenciais inválidas. Tente novamente.')
    }
  }

  return (
    <Box
      className="flex items-center justify-center min-h-screen"
      sx={{ background: 'linear-gradient(135deg, #2e7d32 0%, #00838f 100%)' }}
    >
      <Card sx={{ width: '100%', maxWidth: 420, mx: 2 }}>
        <CardContent className="p-8">
          <Box className="flex flex-col items-center mb-6">
            <Box
              className="flex items-center justify-center w-16 h-16 rounded-full mb-3"
              sx={{ backgroundColor: 'primary.main' }}
            >
              <LocalPharmacyIcon sx={{ color: 'white', fontSize: 32 }} />
            </Box>
            <Typography variant="h5" className="font-bold text-gray-800">
              FarmáciaSys
            </Typography>
            <Typography variant="body2" color="text.secondary" className="mt-1">
              Sistema de Gestão Farmacêutica
            </Typography>
          </Box>

          {error && (
            <Alert severity="error" className="mb-4" onClose={() => setError(null)}>
              {error}
            </Alert>
          )}

          <Box component="form" onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4">
            <TextField
              label="Usuário ou E-mail"
              fullWidth
              {...register('login')}
              error={!!errors.login}
              helperText={errors.login?.message}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <PersonIcon color="action" />
                  </InputAdornment>
                ),
              }}
            />
            <TextField
              label="Senha"
              type="password"
              fullWidth
              {...register('password')}
              error={!!errors.password}
              helperText={errors.password?.message}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <LockIcon color="action" />
                  </InputAdornment>
                ),
              }}
            />
            <Button
              type="submit"
              variant="contained"
              size="large"
              fullWidth
              disabled={isSubmitting}
              sx={{ mt: 1, py: 1.5 }}
            >
              {isSubmitting ? <CircularProgress size={24} color="inherit" /> : 'Entrar'}
            </Button>
          </Box>

          <Box className="text-center mt-4">
            <Typography variant="body2" color="text.secondary">
              Não tem uma conta?{' '}
              <Link
                to="/register"
                style={{ color: '#2e7d32', fontWeight: 600, textDecoration: 'none' }}
              >
                Cadastre-se
              </Link>
            </Typography>
          </Box>
        </CardContent>
      </Card>
    </Box>
  )
}
