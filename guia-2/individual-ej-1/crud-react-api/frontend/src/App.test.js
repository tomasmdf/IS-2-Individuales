import { render, screen } from '@testing-library/react';
import App from './App';

test('renders the products navigation', () => {
  render(<App />);
  expect(screen.getByRole('link', { name: /products list/i })).toBeInTheDocument();
});
