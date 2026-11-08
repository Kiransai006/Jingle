import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { api } from '../api.js';
import { rupees } from '../utils/cart.js';

export default function OrderSuccess() {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    api.order(id).then(setOrder).catch((e) => setError(e.message));
  }, [id]);

  if (error) return <div className="container error">{error}</div>;
  if (!order) return <div className="container empty">Loading order…</div>;

  return (
    <div className="container">
      <div className="panel success">
        <div className="tick">✓</div>
        <h1>Order #{order.id} placed!</h1>
        <p>
          Thanks {order.customerName}, your food is being prepared. Status: <strong>{order.status}</strong>
        </p>
        <div className="bill">
          {order.items.map((i) => (
            <div key={i.id}>
              <span>{i.quantity} × {i.name}</span>
              <span>{rupees(i.price * i.quantity)}</span>
            </div>
          ))}
          <div><span>Delivery fee</span><span>{rupees(order.deliveryFee)}</span></div>
          <div className="bill-total"><span>Paid</span><span>{rupees(order.totalAmount)}</span></div>
        </div>
        <p className="muted">Delivering to: {order.address}</p>
        <Link to="/" className="primary-btn">Order more</Link>
      </div>
    </div>
  );
}
