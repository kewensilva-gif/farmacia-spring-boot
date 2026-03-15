import { createBrowserRouter, Navigate } from 'react-router-dom'
import { ProtectedRoute } from '../shared/components/ProtectedRoute'
import { Layout } from '../shared/components/Layout'
import { LoginPage } from '../modules/auth/pages/LoginPage'
import { RegisterPage } from '../modules/auth/pages/RegisterPage'
import { DashboardPage } from '../modules/dashboard/pages/DashboardPage'
import { CategoriesPage } from '../modules/categories/pages/CategoriesPage'
import { ProductsPage } from '../modules/products/pages/ProductsPage'
import { CustomersPage } from '../modules/customers/pages/CustomersPage'
import { EmployeesPage } from '../modules/employees/pages/EmployeesPage'
import { SalesPage } from '../modules/sales/pages/SalesPage'
import { UsersPage } from '../modules/users/pages/UsersPage'
import { RolesPage } from '../modules/roles/pages/RolesPage'

export const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/register',
    element: <RegisterPage />,
  },
  {
    path: '/',
    element: <Navigate to="/dashboard" replace />,
  },
  {
    element: <ProtectedRoute />,
    children: [
      {
        element: <Layout />,
        children: [
          {
            path: '/dashboard',
            element: <DashboardPage />,
          },
          {
            path: '/categories',
            element: <CategoriesPage />,
          },
          {
            path: '/products',
            element: <ProductsPage />,
          },
          {
            element: <ProtectedRoute allowedRoles={['ADMIN', 'EMPLOYEE']} />,
            children: [
              { path: '/customers', element: <CustomersPage /> },
              { path: '/sales', element: <SalesPage /> },
            ],
          },
          {
            element: <ProtectedRoute allowedRoles={['ADMIN']} />,
            children: [
              { path: '/employees', element: <EmployeesPage /> },
              { path: '/users', element: <UsersPage /> },
              { path: '/roles', element: <RolesPage /> },
            ],
          },
        ],
      },
    ],
  },
])
