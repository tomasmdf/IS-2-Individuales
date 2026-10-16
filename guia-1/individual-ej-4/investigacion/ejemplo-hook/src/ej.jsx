import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import Contador from './Contador';

describe('Pruebas unitarias sobre el componente Contador', () => {
  test('Renderiza el componente con el valor inicial en 0', () => {
    // 1. Renderizar el componente en el DOM simulado
    render(<Contador />);

    // 2. Localizar elementos en pantalla
    const encabezado = screen.getByRole('heading', { name: /sección de conteo/i });
    const textoValor = screen.getByText(/valor: 0/i);

    // 3. Aserciones con Jest
    expect(encabezado).toBeInTheDocument();
    expect(textoValor).toBeInTheDocument();
  });

  test('Incrementa el valor en 1 al pulsar el botón', () => {
    render(<Contador />);

    // Localizar el botón e interactuar simulando un clic del usuario
    const botonIncrementar = screen.getByRole('button', { name: /incrementar/i });
    fireEvent.click(botonIncrementar);

    // Verificar que el estado visual haya cambiado a 1
    const textoValorActualizado = screen.getByText(/valor: 1/i);
    expect(textoValorActualizado).toBeInTheDocument();
  });
});