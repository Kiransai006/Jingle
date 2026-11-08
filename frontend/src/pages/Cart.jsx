import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api } from '../api.js';
import { useCart } from '../CartContext.jsx';
import { DELIVERY_FEE, grandTotal, itemsTotal, rupees } from '../utils/cart.js';

export default function Cart() {
  const cart = useCart();
  const navigate = useNavigate();
  const [form, setForm] = useState({ customerName: '', phone: '', address: '' });
  const [placing, setPlacing] = useState(false);
  const [error, setError] = useState('');

  if (cart.items.length === 0) {
    return (
      <div className="container empty cart-empty">
        <h2>Your cart is empty</h2>
        <p>Good food is always cooking. Go ahead, order some yummy items from the menu.</p>
        <Link to="/" className="primary-btn">See restaurants near you</Link>
      </div>
    );
  }

  const update = (field) => (e) => setForm({ ...form, [field]: e.target.value });

  const placeOrder = async (e) => {
    e.preventDefault();
    setPlacing(true);
    setError('');
    try {
      const order = await api.placeOrder({
        ...form,
        items: cart.items.map((i) => ({ menuItemId: i.id, quantity: i.qty })),
      });
      cart.clear();
      navigate(`/order/${order.id}`);
    } catch (err) {
      setError(err.message);
    } finally {
      setPlacing(false);
    }
  };

  return (
    <div className="container checkout">
      <form className="panel" onSubmit={placeOrder}>
        <h2>Delivery details</h2>
        <label>
          Name
          <input required value={form.customerName} onChange={update('customerName')} placeholder="Your name" />
        </label>
        <label>
          Phone
          <input required value={form.phone} onChange={update('phone')} placeholder="Mobile number" />
        </label>
        <label>
          Address
          <textarea required rows={3} value={form.address} onChange={update('address')} placeholder="House no, street, area" />
        </label>
        {error && <div className="error">{error}</div>}
        <button className="primary-btn" disabled={placing}>
          {placing ? 'Placing order…' : `Place order · ${rupees(grandTotal(cart.items))}`}
        </button>
      </form>

      <div className="panel">
        <h2>{cart.restaurant?.name}</h2>
        {cart.items.map((i) => (
          <div key={i.id} className="cart-row">
            <span className={`veg-mark ${i.veg ? 'veg' : 'nonveg'}`} />
            <span className="cart-name">{i.name}</span>
            <div className="qty small">
              <button type="button" onClick={() => cart.remove(i.id)}>−</button>
              <span>{i.qty}</span>
              <button type="button" onClick={() => cart.add(cart.restaurant, i)}>+</button>
            </div>
            <span className="cart-price">{rupees(i.price * i.qty)}</span>
          </div>
        ))}
        <div className="bill">
          <div><span>Item total</span><span>{rupees(itemsTotal(cart.items))}</span></div>
          <div><span>Delivery fee</span><span>{rupees(DELIVERY_FEE)}</span></div>
          <div className="bill-total"><span>TO PAY</span><span>{rupees(grandTotal(cart.items))}</span></div>
        </div>
      </div>
    </div>
  );
}
