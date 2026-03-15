import { useState } from 'react'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableContainer from '@mui/material/TableContainer'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import Paper from '@mui/material/Paper'
import IconButton from '@mui/material/IconButton'
import Chip from '@mui/material/Chip'
import TextField from '@mui/material/TextField'
import Box from '@mui/material/Box'
import Skeleton from '@mui/material/Skeleton'
import Typography from '@mui/material/Typography'
import Tooltip from '@mui/material/Tooltip'
import Tabs from '@mui/material/Tabs'
import Tab from '@mui/material/Tab'
import EditIcon from '@mui/icons-material/Edit'
import DeleteIcon from '@mui/icons-material/Delete'
import SearchIcon from '@mui/icons-material/Search'
import InputAdornment from '@mui/material/InputAdornment'
import type { Employee } from '../types/employee.types'
import { formatCurrency, formatDate, formatCPF } from '../../../shared/utils/formatters'

interface EmployeeTableProps {
  employees: Employee[]
  loading: boolean
  onEdit: (employee: Employee) => void
  onDelete: (employee: Employee) => void
}

export function EmployeeTable({ employees, loading, onEdit, onDelete }: EmployeeTableProps) {
  const [search, setSearch] = useState('')
  const [tabValue, setTabValue] = useState(0)

  const isActive = (employee: Employee) => !employee.terminationDate

  const filtered = employees.filter((e) => {
    const name = `${e.person?.firstname || ''} ${e.person?.lastname || ''}`.toLowerCase()
    const cpf = e.person?.cpf || ''
    const matchesSearch = name.includes(search.toLowerCase()) || cpf.includes(search)
    const matchesTab = tabValue === 0 ? isActive(e) : !isActive(e)
    return matchesSearch && matchesTab
  })

  return (
    <Box>
      <Box className="flex items-center gap-3 mb-4">
        <TextField
          placeholder="Buscar por nome ou CPF..."
          size="small"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon color="action" fontSize="small" />
              </InputAdornment>
            ),
          }}
          sx={{ minWidth: 280 }}
        />
      </Box>

      <Tabs value={tabValue} onChange={(_, v) => setTabValue(v)} className="mb-3">
        <Tab label="Ativos" />
        <Tab label="Inativos" />
      </Tabs>

      <TableContainer component={Paper} sx={{ borderRadius: 2 }}>
        <Table size="small">
          <TableHead>
            <TableRow sx={{ backgroundColor: '#f5f5f5' }}>
              <TableCell><strong>Nome</strong></TableCell>
              <TableCell><strong>CPF</strong></TableCell>
              <TableCell><strong>Salário</strong></TableCell>
              <TableCell><strong>Contratação</strong></TableCell>
              <TableCell><strong>Demissão</strong></TableCell>
              <TableCell><strong>Status</strong></TableCell>
              <TableCell align="right"><strong>Ações</strong></TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {loading ? (
              Array.from({ length: 5 }).map((_, i) => (
                <TableRow key={i}>
                  {Array.from({ length: 7 }).map((__, j) => (
                    <TableCell key={j}><Skeleton /></TableCell>
                  ))}
                </TableRow>
              ))
            ) : filtered.length === 0 ? (
              <TableRow>
                <TableCell colSpan={7} align="center">
                  <Typography variant="body2" color="text.secondary" className="py-4">
                    Nenhum funcionário encontrado
                  </Typography>
                </TableCell>
              </TableRow>
            ) : (
              filtered.map((employee) => (
                <TableRow key={employee.id} hover>
                  <TableCell>
                    {employee.person?.firstname} {employee.person?.lastname}
                  </TableCell>
                  <TableCell>{formatCPF(employee.person?.cpf)}</TableCell>
                  <TableCell>{formatCurrency(employee.salary)}</TableCell>
                  <TableCell>{formatDate(employee.hiringDate)}</TableCell>
                  <TableCell>{employee.terminationDate ? formatDate(employee.terminationDate) : '-'}</TableCell>
                  <TableCell>
                    <Chip
                      label={isActive(employee) ? 'Ativo' : 'Inativo'}
                      color={isActive(employee) ? 'success' : 'default'}
                      size="small"
                    />
                  </TableCell>
                  <TableCell align="right">
                    <Tooltip title="Editar">
                      <IconButton size="small" color="primary" onClick={() => onEdit(employee)}>
                        <EditIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="Excluir">
                      <IconButton size="small" color="error" onClick={() => onDelete(employee)}>
                        <DeleteIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  )
}
