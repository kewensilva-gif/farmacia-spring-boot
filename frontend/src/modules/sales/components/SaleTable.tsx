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
import MenuItem from '@mui/material/MenuItem'
import EditIcon from '@mui/icons-material/Edit'
import DeleteIcon from '@mui/icons-material/Delete'
import type { Sale, PaymentMethod } from '../types/sale.types'
import { useAuth } from '../../../contexts/AuthContext'
import { formatCurrency, formatPaymentMethod } from '../../../shared/utils/formatters'

const PAYMENT_COLORS: Record<PaymentMethod, 'primary' | 'secondary' | 'success' | 'warning'> = {
  CREDITCARD: 'primary',
  DEBITCARD: 'secondary',
  PIX: 'success',
  CASH: 'warning',
}

interface SaleTableProps {
  sales: Sale[]
  loading: boolean
  onEdit: (sale: Sale) => void
  onDelete: (sale: Sale) => void
}

export function SaleTable({ sales, loading, onEdit, onDelete }: SaleTableProps) {
  const { user } = useAuth()
  const [paymentFilter, setPaymentFilter] = useState<string>('')

  const isAdmin = user?.role === 'ADMIN'
  const canWrite = isAdmin || user?.role === 'EMPLOYEE'

  const filtered = sales.filter((s) => {
    if (paymentFilter && s.paymentMethod !== paymentFilter) return false
    return true
  })

  return (
    <Box>
      <Box className="mb-4">
        <TextField
          select
          label="Filtrar por pagamento"
          size="small"
          value={paymentFilter}
          onChange={(e) => setPaymentFilter(e.target.value)}
          sx={{ minWidth: 200 }}
        >
          <MenuItem value="">Todos</MenuItem>
          <MenuItem value="CREDITCARD">Cartão de Crédito</MenuItem>
          <MenuItem value="DEBITCARD">Cartão de Débito</MenuItem>
          <MenuItem value="PIX">Pix</MenuItem>
          <MenuItem value="CASH">Dinheiro</MenuItem>
        </TextField>
      </Box>

      <TableContainer component={Paper} sx={{ borderRadius: 2 }}>
        <Table size="small">
          <TableHead>
            <TableRow sx={{ backgroundColor: '#f5f5f5' }}>
              <TableCell width={80}><strong>ID</strong></TableCell>
              <TableCell><strong>Total</strong></TableCell>
              <TableCell><strong>Desconto</strong></TableCell>
              <TableCell><strong>Pagamento</strong></TableCell>
              <TableCell><strong>Funcionário</strong></TableCell>
              <TableCell><strong>Cliente</strong></TableCell>
              <TableCell><strong>Status</strong></TableCell>
              {canWrite && <TableCell align="right"><strong>Ações</strong></TableCell>}
            </TableRow>
          </TableHead>
          <TableBody>
            {loading ? (
              Array.from({ length: 5 }).map((_, i) => (
                <TableRow key={i}>
                  {Array.from({ length: canWrite ? 8 : 7 }).map((__, j) => (
                    <TableCell key={j}><Skeleton /></TableCell>
                  ))}
                </TableRow>
              ))
            ) : filtered.length === 0 ? (
              <TableRow>
                <TableCell colSpan={canWrite ? 8 : 7} align="center">
                  <Typography variant="body2" color="text.secondary" className="py-4">
                    Nenhuma venda encontrada
                  </Typography>
                </TableCell>
              </TableRow>
            ) : (
              filtered.map((sale) => (
                <TableRow key={sale.id} hover>
                  <TableCell>{sale.id}</TableCell>
                  <TableCell>
                    <Typography variant="body2" sx={{ fontWeight: 600, color: 'success.main' }}>
                      {formatCurrency(sale.totalPrice)}
                    </Typography>
                  </TableCell>
                  <TableCell>{formatCurrency(sale.discount)}</TableCell>
                  <TableCell>
                    <Chip
                      label={formatPaymentMethod(sale.paymentMethod)}
                      color={PAYMENT_COLORS[sale.paymentMethod] || 'default'}
                      size="small"
                      variant="outlined"
                    />
                  </TableCell>
                  <TableCell>
                    {sale.employee?.person?.firstname} {sale.employee?.person?.lastname}
                  </TableCell>
                  <TableCell>
                    {sale.customer
                      ? `${sale.customer.person?.firstname} ${sale.customer.person?.lastname}`
                      : '-'}
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={sale.enabled ? 'Ativo' : 'Inativo'}
                      color={sale.enabled ? 'success' : 'default'}
                      size="small"
                    />
                  </TableCell>
                  {canWrite && (
                    <TableCell align="right">
                      <Tooltip title="Editar">
                        <IconButton size="small" color="primary" onClick={() => onEdit(sale)}>
                          <EditIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                      {isAdmin && (
                        <Tooltip title="Excluir">
                          <IconButton size="small" color="error" onClick={() => onDelete(sale)}>
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
