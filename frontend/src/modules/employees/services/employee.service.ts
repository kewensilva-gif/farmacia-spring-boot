import api from '../../../lib/axios'
import type { Employee, EmployeeDto } from '../types/employee.types'

export const employeeService = {
  getAll: async (): Promise<Employee[]> => {
    const response = await api.get<Employee[]>('/api/employees')
    return response.data
  },

  getById: async (id: number): Promise<Employee> => {
    const response = await api.get<Employee>(`/api/employees/${id}`)
    return response.data
  },

  create: async (data: EmployeeDto): Promise<Employee> => {
    const response = await api.post<Employee>('/api/employees', data)
    return response.data
  },

  update: async (id: number, data: EmployeeDto): Promise<Employee> => {
    const response = await api.put<Employee>(`/api/employees/${id}`, data)
    return response.data
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/api/employees/${id}`)
  },

  getActive: async (): Promise<Employee[]> => {
    const response = await api.get<Employee[]>('/api/employees/active')
    return response.data
  },

  getInactive: async (): Promise<Employee[]> => {
    const response = await api.get<Employee[]>('/api/employees/inactive')
    return response.data
  },

  searchByDateRange: async (startDate: string, endDate: string): Promise<Employee[]> => {
    const response = await api.get<Employee[]>(
      `/api/employees/search/date?startDate=${startDate}&endDate=${endDate}`,
    )
    return response.data
  },
}
