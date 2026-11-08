import React, { useCallback, useEffect, useState } from 'react';
import { Alert, Box, Button, Card, Dialog, DialogActions, DialogContent, DialogTitle, IconButton, Snackbar, TextField } from '@mui/material';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TablePagination from '@mui/material/TablePagination';
import TableRow from '@mui/material/TableRow';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import { Link } from 'react-router-dom';
import axiosInstance from '../axiosConfig';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';
const emptyProduct = { name: '', description: '', brand: '', acquisitionDate: '', price: '' };
const fields = [
  ['name', 'Name'], ['description', 'Description'], ['brand', 'Brand'],
  ['acquisitionDate', 'Acquisition date'], ['price', 'Price'],
];

export default function Products() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filterText, setFilterText] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(5);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(emptyProduct);
  const [deleteId, setDeleteId] = useState(null);
  const [notice, setNotice] = useState({ open: false, message: '', severity: 'success' });

  const loadProducts = useCallback(async () => {
    setLoading(true);
    try {
      const response = await axiosInstance.get(`${API_URL}/products`);
      setProducts(Array.isArray(response.data) ? response.data : []);
      setError('');
    } catch (requestError) {
      setError('No se pudieron cargar los productos. Comprueba que el servidor esté disponible.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { loadProducts(); }, [loadProducts]);

  const openCreate = () => { setEditing(null); setForm(emptyProduct); setDialogOpen(true); };
  const openEdit = (product) => {
    setEditing(product);
    setForm({ ...emptyProduct, ...product, acquisitionDate: product.acquisitionDate || '', price: product.price ?? '' });
    setDialogOpen(true);
  };
  const closeDialog = () => setDialogOpen(false);
  const changeField = (event) => setForm((current) => ({ ...current, [event.target.name]: event.target.value }));

  const saveProduct = async (event) => {
    event.preventDefault();
    const payload = { ...form, price: Number(form.price) };
    try {
      if (editing) {
        await axiosInstance.put(`${API_URL}/product/${editing.id}`, { ...payload, id: editing.id });
        setNotice({ open: true, message: 'Producto actualizado.', severity: 'success' });
      } else {
        await axiosInstance.post(`${API_URL}/products`, payload);
        setNotice({ open: true, message: 'Producto agregado.', severity: 'success' });
      }
      closeDialog();
      await loadProducts();
    } catch (requestError) {
      setNotice({ open: true, message: 'No se pudo guardar el producto.', severity: 'error' });
    }
  };

  const deleteProduct = async () => {
    try {
      await axiosInstance.delete(`${API_URL}/product/${deleteId}`);
      setDeleteId(null);
      setNotice({ open: true, message: 'Producto eliminado.', severity: 'success' });
      await loadProducts();
    } catch (requestError) {
      setNotice({ open: true, message: 'No se pudo eliminar el producto.', severity: 'error' });
    }
  };

  const filteredProducts = products.filter((product) =>
    [product.name, product.description, product.brand].some((value) =>
      String(value || '').toLowerCase().includes(filterText.toLowerCase())
    )
  );

  return (
    <Box sx={{ display: 'flex', justifyContent: 'center', p: { xs: 1, md: 4 } }}>
      <Card sx={{ width: '100%', maxWidth: 1200, p: { xs: 1, md: 3 } }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', gap: 2, mb: 2, flexWrap: 'wrap' }}>
          <Button variant="contained" onClick={openCreate}>Agregar producto</Button>
          <TextField label="Buscar productos" value={filterText} onChange={(event) => { setFilterText(event.target.value); setPage(0); }} />
        </Box>
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        <TableContainer>
          <Table aria-label="Lista de productos">
            <TableHead><TableRow>
              <TableCell>Nombre</TableCell><TableCell>Descripción</TableCell><TableCell>Marca</TableCell>
              <TableCell>Fecha de adquisición</TableCell><TableCell>Precio</TableCell><TableCell align="right">Acciones</TableCell>
            </TableRow></TableHead>
            <TableBody>
              {loading ? <TableRow><TableCell colSpan={6}>Cargando productos…</TableCell></TableRow>
                : filteredProducts.length === 0 ? <TableRow><TableCell colSpan={6}>No hay productos para mostrar.</TableCell></TableRow>
                : filteredProducts.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map((product) => (
                  <TableRow key={product.id}>
                    <TableCell><Link to={`/product/${product.id}`} state={{ currentProduct: product }}>{product.name}</Link></TableCell>
                    <TableCell>{product.description}</TableCell><TableCell>{product.brand}</TableCell>
                    <TableCell>{product.acquisitionDate || '—'}</TableCell><TableCell>{product.price}</TableCell>
                    <TableCell align="right">
                      <IconButton aria-label={`Editar ${product.name}`} onClick={() => openEdit(product)}><EditIcon /></IconButton>
                      <IconButton aria-label={`Eliminar ${product.name}`} onClick={() => setDeleteId(product.id)}><DeleteIcon /></IconButton>
                    </TableCell>
                  </TableRow>
                ))}
            </TableBody>
          </Table>
        </TableContainer>
        <TablePagination component="div" count={filteredProducts.length} page={page} onPageChange={(event, nextPage) => setPage(nextPage)}
          rowsPerPage={rowsPerPage} onRowsPerPageChange={(event) => { setRowsPerPage(Number(event.target.value)); setPage(0); }} rowsPerPageOptions={[5, 10, 25]} />
      </Card>

      <Dialog open={dialogOpen} onClose={closeDialog} fullWidth maxWidth="sm">
        <Box component="form" onSubmit={saveProduct}>
          <DialogTitle>{editing ? 'Editar producto' : 'Agregar producto'}</DialogTitle>
          <DialogContent>{fields.map(([name, label]) => <TextField key={name} margin="dense" name={name} label={label} fullWidth
            required value={form[name]} onChange={changeField} type={name === 'price' ? 'number' : name === 'acquisitionDate' ? 'date' : 'text'}
            slotProps={name === 'acquisitionDate' ? { inputLabel: { shrink: true } } : name === 'price' ? { htmlInput: { min: 0, step: '0.01' } } : undefined} />)}</DialogContent>
          <DialogActions><Button onClick={closeDialog}>Cancelar</Button><Button type="submit" variant="contained">Guardar</Button></DialogActions>
        </Box>
      </Dialog>
      <Dialog open={deleteId !== null} onClose={() => setDeleteId(null)}>
        <DialogTitle>Eliminar producto</DialogTitle><DialogContent>¿Confirmas que quieres eliminar este producto?</DialogContent>
        <DialogActions><Button onClick={() => setDeleteId(null)}>Cancelar</Button><Button color="error" variant="contained" onClick={deleteProduct}>Eliminar</Button></DialogActions>
      </Dialog>
      <Snackbar open={notice.open} autoHideDuration={4000} onClose={() => setNotice((current) => ({ ...current, open: false }))}>
        <Alert severity={notice.severity} onClose={() => setNotice((current) => ({ ...current, open: false }))}>{notice.message}</Alert>
      </Snackbar>
    </Box>
  );
}
