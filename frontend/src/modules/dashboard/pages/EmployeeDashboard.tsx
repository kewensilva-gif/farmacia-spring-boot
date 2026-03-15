import { useNavigate } from 'react-router-dom'
import Grid from '@mui/material/Grid'
import Card from '@mui/material/Card'
import CardContent from '@mui/material/CardContent'
import CardActionArea from '@mui/material/CardActionArea'
import Typography from '@mui/material/Typography'
import Box from '@mui/material/Box'
import ReceiptIcon from '@mui/icons-material/Receipt'
import PeopleIcon from '@mui/icons-material/People'
import MedicationIcon from '@mui/icons-material/Medication'
import CategoryIcon from '@mui/icons-material/Category'
import { useAuth } from '../../../contexts/AuthContext'

export function EmployeeDashboard() {
  const navigate = useNavigate()
  const { user } = useAuth()

  const quickLinks = [
    {
      label: 'Vendas',
      description: 'Registrar e gerenciar vendas',
      path: '/sales',
      icon: <ReceiptIcon sx={{ fontSize: 40 }} />,
      color: '#e65100',
    },
    {
      label: 'Clientes',
      description: 'Gerenciar clientes',
      path: '/customers',
      icon: <PeopleIcon sx={{ fontSize: 40 }} />,
      color: '#1565c0',
    },
    {
      label: 'Produtos',
      description: 'Consultar estoque',
      path: '/products',
      icon: <MedicationIcon sx={{ fontSize: 40 }} />,
      color: '#2e7d32',
    },
    {
      label: 'Categorias',
      description: 'Ver categorias',
      path: '/categories',
      icon: <CategoryIcon sx={{ fontSize: 40 }} />,
      color: '#00838f',
    },
  ]

  return (
    <Box>
      <Typography variant="h5" className="font-bold text-gray-800 mb-1">
        Bem-vindo, {user?.username}!
      </Typography>
      <Typography variant="body2" color="text.secondary" className="mb-6">
        Painel do Funcionário
      </Typography>

      <Grid container spacing={3}>
        {quickLinks.map((link) => (
          <Grid key={link.path} size={{ xs: 12, sm: 6, md: 3 }}>
            <Card sx={{ borderTop: `4px solid ${link.color}`, height: '100%' }}>
              <CardActionArea onClick={() => navigate(link.path)} sx={{ height: '100%' }}>
                <CardContent className="flex flex-col items-center text-center p-6">
                  <Box
                    className="flex items-center justify-center w-16 h-16 rounded-full mb-3"
                    sx={{ backgroundColor: `${link.color}18` }}
                  >
                    <Box sx={{ color: link.color }}>{link.icon}</Box>
                  </Box>
                  <Typography variant="h6" className="font-semibold mb-1">
                    {link.label}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {link.description}
                  </Typography>
                </CardContent>
              </CardActionArea>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  )
}
