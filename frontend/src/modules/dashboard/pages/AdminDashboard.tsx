import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import Grid from '@mui/material/Grid'
import Card from '@mui/material/Card'
import CardContent from '@mui/material/CardContent'
import Typography from '@mui/material/Typography'
import Box from '@mui/material/Box'
import Skeleton from '@mui/material/Skeleton'
import CardActionArea from '@mui/material/CardActionArea'
import MedicationIcon from '@mui/icons-material/Medication'
import CategoryIcon from '@mui/icons-material/Category'
import PeopleIcon from '@mui/icons-material/People'
import BadgeIcon from '@mui/icons-material/Badge'
import ReceiptIcon from '@mui/icons-material/Receipt'
import api from '../../../lib/axios'

interface StatCard {
  label: string
  value: number | null
  icon: React.ReactNode
  color: string
  path: string
}

export function AdminDashboard() {
  const navigate = useNavigate()
  const [stats, setStats] = useState<Record<string, number | null>>({
    products: null,
    categories: null,
    customers: null,
    employees: null,
    sales: null,
  })
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const [products, categories, customers, employees, sales] = await Promise.allSettled([
          api.get('/api/products'),
          api.get('/api/categories'),
          api.get('/api/customers'),
          api.get('/api/employees'),
          api.get('/api/sales'),
        ])

        setStats({
          products: products.status === 'fulfilled' ? (Array.isArray(products.value.data) ? products.value.data.length : 0) : 0,
          categories: categories.status === 'fulfilled' ? (Array.isArray(categories.value.data) ? categories.value.data.length : 0) : 0,
          customers: customers.status === 'fulfilled' ? (Array.isArray(customers.value.data) ? customers.value.data.length : 0) : 0,
          employees: employees.status === 'fulfilled' ? (Array.isArray(employees.value.data) ? employees.value.data.length : 0) : 0,
          sales: sales.status === 'fulfilled' ? (Array.isArray(sales.value.data) ? sales.value.data.length : 0) : 0,
        })
      } finally {
        setLoading(false)
      }
    }
    fetchStats()
  }, [])

  const statCards: StatCard[] = [
    { label: 'Produtos', value: stats.products, icon: <MedicationIcon sx={{ fontSize: 36 }} />, color: '#2e7d32', path: '/products' },
    { label: 'Categorias', value: stats.categories, icon: <CategoryIcon sx={{ fontSize: 36 }} />, color: '#00838f', path: '/categories' },
    { label: 'Clientes', value: stats.customers, icon: <PeopleIcon sx={{ fontSize: 36 }} />, color: '#1565c0', path: '/customers' },
    { label: 'Funcionários', value: stats.employees, icon: <BadgeIcon sx={{ fontSize: 36 }} />, color: '#6a1b9a', path: '/employees' },
    { label: 'Vendas', value: stats.sales, icon: <ReceiptIcon sx={{ fontSize: 36 }} />, color: '#e65100', path: '/sales' },
  ]

  return (
    <Box>
      <Typography variant="h5" className="font-bold text-gray-800 mb-2">
        Painel Administrativo
      </Typography>
      <Typography variant="body2" color="text.secondary" className="mb-6">
        Visão geral do sistema
      </Typography>

      <Grid container spacing={3} className="mb-6">
        {statCards.map((card) => (
          <Grid key={card.label} size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
            <Card sx={{ borderLeft: `4px solid ${card.color}` }}>
              <CardActionArea onClick={() => navigate(card.path)}>
                <CardContent>
                  <Box className="flex items-center justify-between">
                    <Box>
                      <Typography variant="body2" color="text.secondary">
                        {card.label}
                      </Typography>
                      {loading ? (
                        <Skeleton width={60} height={40} />
                      ) : (
                        <Typography variant="h4" sx={{ color: card.color, fontWeight: 700 }}>
                          {card.value ?? 0}
                        </Typography>
                      )}
                    </Box>
                    <Box
                      className="flex items-center justify-center w-14 h-14 rounded-full"
                      sx={{ backgroundColor: `${card.color}18` }}
                    >
                      <Box sx={{ color: card.color }}>{card.icon}</Box>
                    </Box>
                  </Box>
                </CardContent>
              </CardActionArea>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Typography variant="h6" className="font-semibold text-gray-700 mb-3">
        Acesso Rápido
      </Typography>
      <Grid container spacing={2}>
        {[
          { label: 'Gerenciar Produtos', path: '/products', icon: <MedicationIcon />, color: '#2e7d32' },
          { label: 'Ver Vendas', path: '/sales', icon: <ReceiptIcon />, color: '#e65100' },
          { label: 'Gerenciar Clientes', path: '/customers', icon: <PeopleIcon />, color: '#1565c0' },
          { label: 'Gerenciar Usuários', path: '/users', icon: <BadgeIcon />, color: '#6a1b9a' },
        ].map((item) => (
          <Grid key={item.path} size={{ xs: 12, sm: 6, md: 3 }}>
            <Card>
              <CardActionArea onClick={() => navigate(item.path)} sx={{ p: 2 }}>
                <Box className="flex items-center gap-3">
                  <Box sx={{ color: item.color }}>{item.icon}</Box>
                  <Typography variant="body1" className="font-medium">
                    {item.label}
                  </Typography>
                </Box>
              </CardActionArea>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  )
}
