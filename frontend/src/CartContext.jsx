import { createContext, useContext, useEffect, useState } from 'react';
import { addItem, removeItem } from './utils/cart.js';

const CartContext = createContext(null);
const STORAGE_KEY = 'jingle-cart';

function load() {
  try {
    const saved = JSON.parse(localStorage.getItem(STORAGE_KEY));
    if (saved && Array.isArray(saved.items)) return saved;
  } catch {
    /* ignore */
  }
  return { restaurant: null, items: [] };
}

export function CartProvider({ children }) {
  const [cart, setCart] = useState(load);

  useEffect(() => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(cart));
    } catch {
      /* ignore */
    }
  }, [cart]);

  // Like Swiggy: a cart holds dishes from one restaurant at a time
  const add = (restaurant, item) => {
    const owner = { id: restaurant.id, name: restaurant.name };
    const otherRestaurant = cart.restaurant && cart.restaurant.id !== restaurant.id && cart.items.length > 0;
    if (otherRestaurant) {
      const ok = window.confirm(
        `Your cart has items from ${cart.restaurant.name}. Start a fresh cart from ${restaurant.name}?`
      );
      if (!ok) return;
      setCart({ restaurant: owner, items: addItem([], item) });
      return;
    }
    setCart((prev) => ({ restaurant: owner, items: addItem(prev.items, item) }));
  };

  const remove = (id) =>
    setCart((prev) => {
      const items = removeItem(prev.items, id);
      return { restaurant: items.length ? prev.restaurant : null, items };
    });

  const clear = () => setCart({ restaurant: null, items: [] });

  const qtyOf = (id) => cart.items.find((i) => i.id === id)?.qty || 0;

  return (
    <CartContext.Provider value={{ ...cart, add, remove, clear, qtyOf }}>
      {children}
    </CartContext.Provider>
  );
}

export const useCart = () => useContext(CartContext);
