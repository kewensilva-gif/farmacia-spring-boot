import { useState } from 'react'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableContainer from '@mui/material/TableContainer'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import Paper from '@mui/material/Paper'
import IconButton from '@mui/material/IconButton'
import TextField from '@mui/material/TextField'
import Box from '@mui/material/Box'
import Skeleton from '@mui/material/Skeleton'
import Typography from '@mui/material/Typography'
import Tooltip from '@mui/material/Tooltip'
import EditIcon from '@mui/icons-material/Edit'
import DeleteIcon from '@mui/icons-material/Delete'
import SearchIcon from '@mui/icons-material/Search'
import InputAdornment from '@mui/material/InputAdornment'
import type { Customer } from '../types/customer.types'
import { useAuth } from '../../../contexts/AuthContext'
import { formatDate, formatCPF } from '../../../shared/utils/formatters'

interface CustomerTableProps {
  customers: Customer[]
  loading: boolean
  onEdit: (customer: Customer) => void
  onDelete: (customer: Customer) => void
}

export function CustomerTable({ customers, loading, onEdit, onDelete }: CustomerTableProps) {
  const { user } = useAuth()
  const [search, setSearch] = useState('')

  const isAdmin = user?.role === 'ADMIN'
  const canEdit = user?.role === 'ADMIN' || user?.role === 'EMPLOYEE'

  const filtered = customers.filter((c) => {
    const name = `${c.person?.firstname || ''} ${c.person?.lastname || ''}`.toLowerCase()
    const cpf = c.person?.cpf || ''
    return name.includes(search.toLowerCase()) || cpf.includes(search)
  })

  return (
    <Box>
      <Box className="mb-4">
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

      <TableContainer component={Paper} sx={{ borderRadius: 2 }}>
        <Table size="small">
          <TableHead>
            <TableRow sx={{ backgroundColor: '#f5f5f5' }}>
              <TableCell><strong>Nome</strong></TableCell>
              <TableCell><strong>CPF</strong></TableCell>
              <TableCell><strong>Usuário</strong></TableCell>
              <TableCell><strong>E-mail</strong></TableCell>
              <TableCell><strong>Data de Registro</strong></TableCell>
              {canEdit && <TableCell align="right"><strong>Ações</strong></TableCell>}
            </TableRow>
          </TableHead>
          <TableBody>
            {loading ? (
              Array.from({ length: 5 }).map((_, i) => (
                <TableRow key={i}>
                  {Array.from({ length: canEdit ? 6 : 5 }).map((__, j) => (
                    <TableCell key={j}><Skeleton /></TableCell>
                  ))}
                </TableRow>
              ))
            ) : filtered.length === 0 ? (
              <TableRow>
                <TableCell colSpan={canEdit ? 6 : 5} align="center">
                  <Typography variant="body2" color="text.secondary" className="py-4">
                    Nenhum cliente encontrado
                  </Typography>
                </TableCell>
              </TableRow>
            ) : (
              filtered.map((customer) => (
                <TableRow key={customer.id} hover>
                  <TableCell>
                    {customer.person?.firstname} {customer.person?.lastname}
                  </TableCell>
                  <TableCell>{formatCPF(customer.person?.cpf)}</TableCell>
                  <TableCell>{customer.person?.user?.username || '-'}</TableCell>
                  <TableCell>{customer.person?.user?.email || '-'}</TableCell>
                  <TableCell>{formatDate(customer.registrationDate)}</TableCell>
                  {canEdit && (
                    <TableCell align="right">
                      <Tooltip title="Editar">
                        <IconButton size="small" color="primary" onClick={() => onEdit(customer)}>
                          <EditIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                      {isAdmin && (
                        <Tooltip title="Excluir">
                          <IconButton size="small" color="error" onClick={() => onDelete(customer)}>
                            <DeleteIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      )}
                    </TableCell>
                  )}
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  )
}
