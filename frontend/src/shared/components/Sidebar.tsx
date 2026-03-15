import { useLocation, useNavigate } from 'react-router-dom'
import Drawer from '@mui/material/Drawer'
import List from '@mui/material/List'
import ListItem from '@mui/material/ListItem'
import ListItemButton from '@mui/material/ListItemButton'
import ListItemIcon from '@mui/material/ListItemIcon'
import ListItemText from '@mui/material/ListItemText'
import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'
import Divider from '@mui/material/Divider'
import Toolbar from '@mui/material/Toolbar'
import DashboardIcon from '@mui/icons-material/Dashboard'
import CategoryIcon from '@mui/icons-material/Category'
import MedicationIcon from '@mui/icons-material/Medication'
import PeopleIcon from '@mui/icons-material/People'
import BadgeIcon from '@mui/icons-material/Badge'
import ReceiptIcon from '@mui/icons-material/Receipt'
import ManageAccountsIcon from '@mui/icons-material/ManageAccounts'
import SecurityIcon from '@mui/icons-material/Security'
import LogoutIcon from '@mui/icons-material/Logout'
import LocalPharmacyIcon from '@mui/icons-material/LocalPharmacy'
import { useAuth } from '../../contexts/AuthContext'

const DRAWER_WIDTH = 240

interface NavItem {
  label: string
  path: string
  icon: React.ReactNode
  roles: string[]
}

const navItems: NavItem[] = [
  { label: 'Dashboard', path: '/dashboard', icon: <DashboardIcon />, roles: ['ADMIN', 'EMPLOYEE', 'CUSTOMER'] },
  { label: 'Produtos', path: '/products', icon: <MedicationIcon />, roles: ['ADMIN', 'EMPLOYEE', 'CUSTOMER'] },
  { label: 'Categorias', path: '/categories', icon: <CategoryIcon />, roles: ['ADMIN', 'EMPLOYEE', 'CUSTOMER'] },
  { label: 'Clientes', path: '/customers', icon: <PeopleIcon />, roles: ['ADMIN', 'EMPLOYEE'] },
  { label: 'Vendas', path: '/sales', icon: <ReceiptIcon />, roles: ['ADMIN', 'EMPLOYEE'] },
  { label: 'Funcionários', path: '/employees', icon: <BadgeIcon />, roles: ['ADMIN'] },
  { label: 'Usuários', path: '/users', icon: <ManageAccountsIcon />, roles: ['ADMIN'] },
  { label: 'Perfis', path: '/roles', icon: <SecurityIcon />, roles: ['ADMIN'] },
]

interface SidebarProps {
  open?: boolean
  onClose?: () => void
  variant?: 'permanent' | 'temporary'
}

export function Sidebar({ open, onClose, variant = 'permanent' }: SidebarProps) {
  const { user, logout } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()

  const filteredItems = navItems.filter(
    (item) => user && item.roles.includes(user.role),
  )

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const drawerContent = (
    <Box className="flex flex-col h-full">
      <Toolbar />
      <Box className="px-4 py-3">
        <Box className="flex items-center gap-2">
          <LocalPharmacyIcon sx={{ color: 'primary.main', fontSize: 28 }} />
          <Typography
            variant="h6"
            sx={{ color: 'primary.main', fontWeight: 700, fontSize: '1.1rem' }}
          >
            FarmáciaSys
          </Typography>
        </Box>
      </Box>
      <Divider />
      <List className="flex-1 px-2 py-2">
        {filteredItems.map((item) => {
          const isActive = location.pathname === item.path
          return (
            <ListItem key={item.path} disablePadding className="mb-1">
              <ListItemButton
                onClick={() => navigate(item.path)}
                selected={isActive}
                sx={{
                  borderRadius: 2,
                  '&.Mui-selected': {
                    backgroundColor: 'primary.main',
                    color: 'white',
                    '& .MuiListItemIcon-root': { color: 'white' },
                    '&:hover': { backgroundColor: 'primary.dark' },
                  },
                }}
              >
                <ListItemIcon
                  sx={{
                    minWidth: 36,
                    color: isActive ? 'white' : 'text.secondary',
                  }}
                >
                  {item.icon}
                </ListItemIcon>
                <ListItemText
                  primary={item.label}
                  primaryTypographyProps={{ fontSize: '0.9rem' }}
                />
              </ListItemButton>
            </ListItem>
          )
        })}
      </List>
      <Divider />
      <List className="px-2 py-2">
        <ListItem disablePadding>
          <ListItemButton
            onClick={handleLogout}
            sx={{ borderRadius: 2, color: 'error.main' }}
          >
            <ListItemIcon sx={{ minWidth: 36, color: 'error.main' }}>
              <LogoutIcon />
            </ListItemIcon>
            <ListItemText
              primary="Sair"
              primaryTypographyProps={{ fontSize: '0.9rem' }}
            />
          </ListItemButton>
        </ListItem>
      </List>
    </Box>
  )

  return (
    <Drawer
      variant={variant}
      open={variant === 'temporary' ? open : true}
      onClose={onClose}
      sx={{
        width: DRAWER_WIDTH,
        flexShrink: 0,
        '& .MuiDrawer-paper': {
          width: DRAWER_WIDTH,
          boxSizing: 'border-box',
          borderRight: '1px solid rgba(0,0,0,0.08)',
        },
      }}
    >
      {drawerContent}
    </Drawer>
  )
}
