import AppBar from '@mui/material/AppBar'
import Toolbar from '@mui/material/Toolbar'
import Typography from '@mui/material/Typography'
import Avatar from '@mui/material/Avatar'
import Box from '@mui/material/Box'
import Chip from '@mui/material/Chip'
import IconButton from '@mui/material/IconButton'
import MenuIcon from '@mui/icons-material/Menu'
import { useAuth } from '../../contexts/AuthContext'

interface NavbarProps {
  title: string
  onMenuToggle?: () => void
}

const roleColors: Record<string, 'success' | 'primary' | 'secondary'> = {
  ADMIN: 'success',
  EMPLOYEE: 'primary',
  CUSTOMER: 'secondary',
}

const roleLabels: Record<string, string> = {
  ADMIN: 'Admin',
  EMPLOYEE: 'Funcionário',
  CUSTOMER: 'Cliente',
}

export function Navbar({ title, onMenuToggle }: NavbarProps) {
  const { user } = useAuth()

  const initials = user?.username
    ? user.username.slice(0, 2).toUpperCase()
    : '?'

  return (
    <AppBar
      position="fixed"
      sx={{
        zIndex: (theme) => theme.zIndex.drawer + 1,
        backgroundColor: '#ffffff',
        color: 'text.primary',
        boxShadow: '0 1px 4px rgba(0,0,0,0.12)',
      }}
    >
      <Toolbar className="gap-3">
        <IconButton
          edge="start"
          onClick={onMenuToggle}
          sx={{ display: { sm: 'none' }, mr: 1 }}
        >
          <MenuIcon />
        </IconButton>
        <Typography variant="h6" component="h1" className="flex-1 font-semibold text-gray-800">
          {title}
        </Typography>
        <Box className="flex items-center gap-3">
          {user && (
            <>
              <Box className="text-right hidden sm:block">
                <Typography variant="body2" className="font-medium text-gray-800">
                  {user.username}
                </Typography>
                <Chip
                  label={roleLabels[user.role] || user.role}
                  color={roleColors[user.role] || 'default'}
                  size="small"
                  sx={{ height: 18, fontSize: '0.65rem' }}
                />
              </Box>
              <Avatar
                sx={{
                  bgcolor: 'primary.main',
                  width: 36,
                  height: 36,
                  fontSize: '0.875rem',
                }}
              >
                {initials}
              </Avatar>
            </>
          )}
        </Box>
      </Toolbar>
    </AppBar>
  )
}
