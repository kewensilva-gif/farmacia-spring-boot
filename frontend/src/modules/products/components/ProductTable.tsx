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
import FormControlLabel from '@mui/material/FormControlLabel'
import Checkbox from '@mui/material/Checkbox'
import Tooltip from '@mui/material/Tooltip'
import EditIcon from '@mui/icons-material/Edit'
import DeleteIcon from '@mui/icons-material/Delete'
import SearchIcon from '@mui/icons-material/Search'
import InputAdornment from '@mui/material/InputAdornment'
import WarningIcon from '@mui/icons-material/Warning'
import type { Product } from '../types/product.types'
import { useAuth } from '../../../contexts/AuthContext'
import { formatCurrency, formatDate } from '../../../shared/utils/formatters'

interface ProductTableProps {
  products: Product[]
  loading: boolean
  onEdit: (product: Product) => void
  onDelete: (product: Product) => void
}

export function ProductTable({ products, loading, onEdit, onDelete }: ProductTableProps) {
  const { user } = useAuth()
  const [search, setSearch] = useState('')
  const [showExpiredOnly, setShowExpiredOnly] = useState(false)
  const [showLowStockOnly, setShowLowStockOnly] = useState(false)

  const isAdmin = user?.role === 'ADMIN'
  const today = new Date()

  const filtered = products.filter((p) => {
    const matchesSearch =
      p.name.toLowerCase().includes(search.toLowerCase()) ||
      p.barcode.toLowerCase().includes(search.toLowerCase())
    const isExpired = new Date(p.expirationDate) < today
    const isLowStock = p.stockQuantity <= 10
    if (showExpiredOnly && !isExpired) return false
    if (showLowStockOnly && !isLowStock) return false
    return matchesSearch
  })

  return (
    <Box>
      <Box className="flex flex-wrap items-center gap-3 mb-4">
        <TextField
          placeholder="Buscar por nome ou código..."
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
        <FormControlLabel
          control={
            <Checkbox
              checked={showExpiredOnly}
              onChange={(e) => setShowExpiredOnly(e.target.checked)}
              size="small"
            />
          }
          label={<Typography variant="body2">Expirados</Typography>}
        />
        <FormControlLabel
          control={
            <Checkbox
              checked={showLowStockOnly}
              onChange={(e) => setShowLowStockOnly(e.target.checked)}
              size="small"
            />
          }
          label={<Typography variant="body2">Estoque baixo</Typography>}
        />
      </Box>

      <TableContainer component={Paper} sx={{ borderRadius: 2 }}>
        <Table size="small">
          <TableHead>
            <TableRow sx={{ backgroundColor: '#f5f5f5' }}>
              <TableCell><strong>Nome</strong></TableCell>
              <TableCell><strong>Preço</strong></TableCell>
              <TableCell><strong>Código</strong></TableCell>
              <TableCell><strong>Estoque</strong></TableCell>
              <TableCell><strong>Validade</strong></TableCell>
              <TableCell><strong>Categoria</strong></TableCell>
              <TableCell><strong>Status</strong></TableCell>
              {isAdmin && <TableCell align="right"><strong>Ações</strong></TableCell>}
            </TableRow>
          </TableHead>
          <TableBody>
            {loading ? (
              Array.from({ length: 6 }).map((_, i) => (
                <TableRow key={i}>
                  {Array.from({ length: isAdmin ? 8 : 7 }).map((__, j) => (
                    <TableCell key={j}><Skeleton /></TableCell>
                  ))}
                </TableRow>
              ))
            ) : filtered.length === 0 ? (
              <TableRow>
                <TableCell colSpan={isAdmin ? 8 : 7} align="center">
                  <Typography variant="body2" color="text.secondary" className="py-4">
                    Nenhum produto encontrado
                  </Typography>
                </TableCell>
              </TableRow>
            ) : (
              filtered.map((product) => {
                const isExpired = new Date(product.expirationDate) < today
                const isLowStock = product.stockQuantity <= 10
                return (
                  <TableRow key={product.id} hover>
                    <TableCell>
                      <Box className="flex items-center gap-1">
                        {product.name}
                      </Box>
                    </TableCell>
                    <TableCell>{formatCurrency(product.unitPrice)}</TableCell>
                    <TableCell>
                      <Typography variant="body2" sx={{ fontFamily: 'monospace' }}>
                        {product.barcode}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Box className="flex items-center gap-1">
                        {isLowStock && (
                          <Tooltip title="Estoque baixo">
                            <WarningIcon fontSize="small" color="warning" />
                          </Tooltip>
                        )}
                        <Typography
                          variant="body2"
                          sx={{ color: isLowStock ? 'warning.main' : 'inherit', fontWeight: isLowStock ? 600 : 400 }}
                        >
                          {product.stockQuantity}
                        </Typography>
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Typography
                        variant="body2"
                        sx={{ color: isExpired ? 'error.main' : 'inherit', fontWeight: isExpired ? 600 : 400 }}
                      >
                        {formatDate(product.expirationDate)}
                        {isExpired && ' (Expirado)'}
                      </Typography>
                    </TableCell>
                    <TableCell>{product.category?.name || '-'}</TableCell>
                    <TableCell>
                      <Chip
                        label={product.enabled ? 'Ativo' : 'Inativo'}
                        color={product.enabled ? 'success' : 'default'}
                        size="small"
                      />
                    </TableCell>
                    {isAdmin && (
                      <TableCell align="right">
                        <Tooltip title="Editar">
                          <IconButton size="small" color="primary" onClick={() => onEdit(product)}>
                            <EditIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Excluir">
                          <IconButton size="small" color="error" onClick={() => onDelete(product)}>
                            <DeleteIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      </TableCell>
                    )}
                  </TableRow>
                )
              })
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  )
}
