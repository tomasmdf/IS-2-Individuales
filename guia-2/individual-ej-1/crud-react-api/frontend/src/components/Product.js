import React, { useEffect, useState } from 'react';
import { Alert, Button, Card, CardActions, CardContent, Typography } from '@mui/material';
import { Link, useLocation, useParams } from 'react-router-dom';
import axiosInstance from '../axiosConfig';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

export default function Product() {
  const { id } = useParams();
  const location = useLocation();
  const [product, setProduct] = useState(location.state?.currentProduct || null);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!id || product?.id === Number(id)) return;
    axiosInstance.get(`${API_URL}/product/${id}`)
      .then((response) => setProduct(response.data))
      .catch(() => setError('No se pudieron cargar los datos del producto.'));
  }, [id, product?.id]);

  if (error) return <Alert severity="error" sx={{ m: 3 }}>{error}</Alert>;
  if (!product) return <Alert severity="info" sx={{ m: 3 }}>Selecciona un producto desde la lista.</Alert>;

  return (
    <Card sx={{ maxWidth: 800, m: '30px auto', p: 2 }}>
      <CardContent>
        <Typography variant="h4" gutterBottom>{product.name}</Typography>
        <Typography variant="subtitle1" color="text.secondary">Marca: {product.brand || '—'}</Typography>
        <Typography variant="body1" sx={{ mt: 2 }}>{product.description || 'Sin descripción.'}</Typography>
        <Typography sx={{ mt: 2 }}>Fecha de adquisición: {product.acquisitionDate || '—'}</Typography>
        <Typography>Precio: {product.price ?? '—'}</Typography>
      </CardContent>
      <CardActions><Button component={Link} to="/products">Volver a productos</Button></CardActions>
    </Card>
  );
}
