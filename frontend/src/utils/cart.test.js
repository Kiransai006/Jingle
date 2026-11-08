import { describe, it, expect } from 'vitest';
import { addItem, removeItem, itemsTotal, grandTotal, itemCount, DELIVERY_FEE } from './cart.js';

const pesarattu = { id: 1, name: 'Pesarattu', price: 89 };
const pulihora = { id: 2, name: 'Pulihora', price: 129 };

describe('cart utils', () => {
  it('adds new items and increments existing ones', () => {
    let cart = addItem([], pesarattu);
    cart = addItem(cart, pesarattu);
    cart = addItem(cart, pulihora);
    expect(cart).toHaveLength(2);
    expect(itemCount(cart)).toBe(3);
  });

  it('removes items when quantity reaches zero', () => {
    let cart = addItem([], pesarattu);
    cart = removeItem(cart, 1);
    expect(cart).toHaveLength(0);
  });

  it('calculates totals with delivery fee', () => {
    const cart = [{ ...pesarattu, qty: 2 }, { ...pulihora, qty: 1 }];
    expect(itemsTotal(cart)).toBe(307);
    expect(grandTotal(cart)).toBe(307 + DELIVERY_FEE);
    expect(grandTotal([])).toBe(0);
  });
});
