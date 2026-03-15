import { useState } from 'react'
import { Outlet, useLocation } from 'react-router-dom'
import Box from '@mui/material/Box'
import Toolbar from '@mui/material/Toolbar'
import { Sidebar } from './Sidebar'
import { Navbar } from './Navbar'

const DRAWER_WIDTH = 240

const pageTitles: Record<string, string> = {
  '/dashboard': 'Dashboard',
  '/categories': 'Categorias',
  '/products': 'Produtos',
  '/customers': 'Clientes',
  '/employees': 'Funcionários',
  '/sales': 'Vendas',
  '/users': 'Usuários',
  '/roles': 'Perfis de Acesso',
}

export function Layout() {
  const [mobileOpen, setMobileOpen] = useState(false)
  const location = useLocation()

  const title = pageTitles[location.pathname] || 'FarmáciaSys'

  return (
    <Box className="flex min-h-screen">
      <Navbar title={title} onMenuToggle={() => setMobileOpen(true)} />

      {/* Desktop sidebar */}
      <Box sx={{ display: { xs: 'none', sm: 'block' } }}>
        <Sidebar variant="permanent" />
      </Box>

      {/* Mobile sidebar */}
      <Box sx={{ display: { xs: 'block', sm: 'none' } }}>
        <Sidebar
          variant="temporary"
          open={mobileOpen}
          onClose={() => setMobileOpen(false)}
        />
      </Box>

      <Box
        component="main"
        sx={{
          flexGrow: 1,
          width: { sm: `calc(100% - ${DRAWER_WIDTH}px)` },
          minHeight: '100vh',
          backgroundColor: 'background.default',
        }}
      >
        <Toolbar />
        <Box className="p-6">
          <Outlet />
        </Box>
      </Box>
    </Box>
  )
}
