import { Link } from 'react-router-dom';
import { useCart } from '../CartContext.jsx';
import { itemCount } from '../utils/cart.js';

export default function Header() {
  const { items } = useCart();
  const count = itemCount(items);

  return (
    <header className="header">
      <div className="container header-inner">
        <Link to="/" className="logo">
          <span className="logo-mark">J</span>
          <span className="logo-text">jingle</span>
        </Link>
        <div className="header-location">
          <span className="loc-label">Delivering in</span>
          <span className="loc-value">Hyderabad · Vijayawada · Guntur</span>
        </div>
        <nav className="nav">
          <Link to="/">Restaurants</Link>
          <Link to="/cart" className="cart-link">
            Cart
            {count > 0 && <span className="badge">{count}</span>}
          </Link>
        </nav>
      </div>
    </header>
  );
}
