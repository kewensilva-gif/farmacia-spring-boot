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
import { useAuth } from '../../../contexts/AuthContext'

const registerSchema = z
  .object({
    username: z
      .string()
      .min(3, 'Usuário deve ter pelo menos 3 caracteres')
      .max(50, 'Usuário deve ter no máximo 50 caracteres'),
    email: z.string().min(1, 'E-mail é obrigatório').email('E-mail inválido'),
    password: z
      .string()
      .min(6, 'Senha deve ter pelo menos 6 caracteres')
      .max(100, 'Senha muito longa'),
    confirmPassword: z.string().min(1, 'Confirme sua senha'),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: 'As senhas não coincidem',
    path: ['confirmPassword'],
  })

type RegisterFormData = z.infer<typeof registerSchema>

export function RegisterPage() {
  const { register: registerUser } = useAuth()
  const navigate = useNavigate()
  const [error, setError] = useState<string | null>(null)

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  })

  const onSubmit = async (data: RegisterFormData) => {
    setError(null)
    try {
      await registerUser(data.username, data.email, data.password)
      navigate('/dashboard')
    } catch (err: unknown) {
      const e = err as { response?: { data?: { message?: string } } }
      setError(e?.response?.data?.message || 'Erro ao criar conta. Tente novamente.')
    }
  }

  return (
    <Box
      className="flex items-center justify-center min-h-screen py-8"
      sx={{ background: 'linear-gradient(135deg, #2e7d32 0%, #00838f 100%)' }}
    >
      <Card sx={{ width: '100%', maxWidth: 440, mx: 2 }}>
        <CardContent className="p-8">
          <Box className="flex flex-col items-center mb-6">
            <Box
              className="flex items-center justify-center w-16 h-16 rounded-full mb-3"
              sx={{ backgroundColor: 'primary.main' }}
            >
              <LocalPharmacyIcon sx={{ color: 'white', fontSize: 32 }} />
            </Box>
            <Typography variant="h5" className="font-bold text-gray-800">
              Criar Conta
            </Typography>
            <Typography variant="body2" color="text.secondary" className="mt-1">
              Registre-se como cliente
            </Typography>
          </Box>

          {error && (
            <Alert severity="error" className="mb-4" onClose={() => setError(null)}>
              {error}
            </Alert>
          )}

          <Box component="form" onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4">
            <TextField
              label="Nome de Usuário"
              fullWidth
              {...register('username')}
              error={!!errors.username}
              helperText={errors.username?.message}
            />
            <TextField
              label="E-mail"
              type="email"
              fullWidth
              {...register('email')}
              error={!!errors.email}
              helperText={errors.email?.message}
            />
            <TextField
              label="Senha"
              type="password"
              fullWidth
              {...register('password')}
              error={!!errors.password}
              helperText={errors.password?.message}
            />
            <TextField
              label="Confirmar Senha"
              type="password"
              fullWidth
              {...register('confirmPassword')}
              error={!!errors.confirmPassword}
              helperText={errors.confirmPassword?.message}
            />
            <Button
              type="submit"
              variant="contained"
              size="large"
              fullWidth
              disabled={isSubmitting}
              sx={{ mt: 1, py: 1.5 }}
            >
              {isSubmitting ? <CircularProgress size={24} color="inherit" /> : 'Cadastrar'}
            </Button>
          </Box>

          <Box className="text-center mt-4">
            <Typography variant="body2" color="text.secondary">
              Já tem uma conta?{' '}
              <Link
                to="/login"
                style={{ color: '#2e7d32', fontWeight: 600, textDecoration: 'none' }}
              >
                Faça login
              </Link>
            </Typography>
          </Box>
        </CardContent>
      </Card>
    </Box>
  )
}
