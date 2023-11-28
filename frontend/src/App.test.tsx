import React from 'react';
import { render, screen } from '@testing-library/react';
import App from './App';

test('Renders header', () => {
  render(<App />);
  const elements = screen.getAllByText(/Home/i);
  expect(elements[0]).toBeInTheDocument();
});
