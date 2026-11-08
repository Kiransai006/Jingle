// Must match OrderService.DELIVERY_FEE in the backend
export const DELIVERY_FEE = 30;

export function itemCount(items) {
  return items.reduce((sum, i) => sum + i.qty, 0);
}

export function itemsTotal(items) {
  return items.reduce((sum, i) => sum + i.price * i.qty, 0);
}

export function grandTotal(items) {
  return items.length === 0 ? 0 : itemsTotal(items) + DELIVERY_FEE;
}

export function addItem(items, item) {
  const existing = items.find((i) => i.id === item.id);
  if (existing) {
    return items.map((i) => (i.id === item.id ? { ...i, qty: i.qty + 1 } : i));
  }
  return [...items, { ...item, qty: 1 }];
}

export function removeItem(items, id) {
  return items
    .map((i) => (i.id === id ? { ...i, qty: i.qty - 1 } : i))
    .filter((i) => i.qty > 0);
}

export const rupees = (n) => `₹${Math.round(n)}`;
