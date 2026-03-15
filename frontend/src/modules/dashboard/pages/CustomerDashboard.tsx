import { useNavigate } from 'react-router-dom'
import Grid from '@mui/material/Grid'
import Card from '@mui/material/Card'
import CardContent from '@mui/material/CardContent'
import CardActionArea from '@mui/material/CardActionArea'
import Typography from '@mui/material/Typography'
import Box from '@mui/material/Box'
import MedicationIcon from '@mui/icons-material/Medication'
import CategoryIcon from '@mui/icons-material/Category'
import ReceiptLongIcon from '@mui/icons-material/ReceiptLong'
import { useAuth } from '../../../contexts/AuthContext'

export function CustomerDashboard() {
  const navigate = useNavigate()
  const { user } = useAuth()

  const quickLinks = [
    {
      label: 'Produtos',
      description: 'Navegue pelo nosso catálogo de produtos',
      path: '/products',
      icon: <MedicationIcon sx={{ fontSize: 40 }} />,
      color: '#2e7d32',
    },
    {
      label: 'Categorias',
      description: 'Explore produtos por categoria',
      path: '/categories',
      icon: <CategoryIcon sx={{ fontSize: 40 }} />,
      color: '#00838f',
    },
    {
      label: 'Meus Pedidos',
      description: 'Histórico de compras (em breve)',
      path: '#',
      icon: <ReceiptLongIcon sx={{ fontSize: 40 }} />,
      color: '#9e9e9e',
    },
  ]

  return (
    <Box>
      <Typography variant="h5" className="font-bold text-gray-800 mb-1">
        Olá, {user?.username}!
      </Typography>
      <Typography variant="body2" color="text.secondary" className="mb-6">
        Bem-vindo à FarmáciaSys
      </Typography>

      <Card className="mb-6" sx={{ background: 'linear-gradient(135deg, #2e7d32 0%, #00838f 100%)', color: 'white' }}>
        <CardContent className="p-6">
          <Typography variant="h6" sx={{ color: 'white', fontWeight: 700 }}>
            Sua saúde em primeiro lugar
          </Typography>
          <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.85)', mt: 1 }}>
            Encontre todos os medicamentos e produtos de saúde que você precisa.
          </Typography>
        </CardContent>
      </Card>

      <Typography variant="h6" className="font-semibold text-gray-700 mb-3">
        O que deseja fazer?
      </Typography>

      <Grid container spacing={3}>
        {quickLinks.map((link) => (
          <Grid key={link.path} size={{ xs: 12, sm: 4 }}>
            <Card sx={{ borderTop: `4px solid ${link.color}`, height: '100%' }}>
              <CardActionArea
                onClick={() => link.path !== '#' && navigate(link.path)}
                sx={{ height: '100%', cursor: link.path === '#' ? 'default' : 'pointer' }}
                disabled={link.path === '#'}
              >
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
