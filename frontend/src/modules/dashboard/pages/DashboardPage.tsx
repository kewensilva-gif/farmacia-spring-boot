import { useAuth } from '../../../contexts/AuthContext'
import { AdminDashboard } from './AdminDashboard'
import { EmployeeDashboard } from './EmployeeDashboard'
import { CustomerDashboard } from './CustomerDashboard'

export function DashboardPage() {
  const { user } = useAuth()

  if (user?.role === 'ADMIN') return <AdminDashboard />
  if (user?.role === 'EMPLOYEE') return <EmployeeDashboard />
  return <CustomerDashboard />
}
