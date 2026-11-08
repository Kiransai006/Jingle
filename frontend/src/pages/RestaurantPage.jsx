import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { api } from '../api.js';
import { useCart } from '../CartContext.jsx';
import FoodImage from '../components/FoodImage.jsx';
import { itemCount, itemsTotal, rupees } from '../utils/cart.js';

export default function RestaurantPage() {
  const { id } = useParams();
  const [restaurant, setRestaurant] = useState(null);
  const [menu, setMenu] = useState([]);
  const [vegOnly, setVegOnly] = useState(false);
  const [error, setError] = useState('');
  const cart = useCart();

  useEffect(() => {
    Promise.all([api.restaurant(id), api.menu(id)])
      .then(([r, m]) => {
        setRestaurant(r);
        setMenu(m);
      })
      .catch((e) => setError(e.message));
  }, [id]);

  if (error) return <div className="container error">{error}</div>;
  if (!restaurant) return <div className="container empty">Loading menu…</div>;

  const dishes = vegOnly ? menu.filter((m) => m.veg) : menu;
  const cartHere = cart.restaurant?.id === restaurant.id && cart.items.length > 0;

  return (
    <div className="container">
      <Link to="/" className="back">← All restaurants</Link>

      <section className="r-header">
        <div>
          <h1>{restaurant.name}</h1>
          <p className="r-cuisine">{restaurant.cuisine}</p>
          <p className="r-area">{restaurant.area}</p>
        </div>
        <div className="r-stats">
          <div><strong>★ {restaurant.rating.toFixed(1)}</strong><span>Rating</span></div>
          <div><strong>{restaurant.deliveryMinutes} min</strong><span>Delivery</span></div>
          <div><strong>{rupees(restaurant.costForTwo)}</strong><span>For two</span></div>
        </div>
      </section>
      {restaurant.offer && <div className="offer-strip">🏷️ {restaurant.offer}</div>}

      <div className="menu-toolbar">
        <h2 className="section-title">Menu</h2>
        <label className="veg-toggle">
          <input type="checkbox" checked={vegOnly} onChange={(e) => setVegOnly(e.target.checked)} />
          Veg only
        </label>
      </div>

      <div className="menu">
        {dishes.map((d) => {
          const qty = cart.qtyOf(d.id);
          return (
            <div key={d.id} className="dish">
              <div className="dish-info">
                <span className={`veg-mark ${d.veg ? 'veg' : 'nonveg'}`} title={d.veg ? 'Veg' : 'Non-veg'} />
                {d.bestseller && <span className="bestseller">★ Bestseller</span>}
                <h3>{d.name}</h3>
                <div className="price">{rupees(d.price)}</div>
                <p>{d.description}</p>
              </div>
              <div className="dish-side">
                <FoodImage src={d.imageUrl} alt={d.name} className="dish-img" />
                {qty === 0 || cart.restaurant?.id !== restaurant.id ? (
                  <button className="add-btn" onClick={() => cart.add(restaurant, { id: d.id, name: d.name, price: d.price, veg: d.veg })}>
                    ADD
                  </button>
                ) : (
                  <div className="qty">
                    <button onClick={() => cart.remove(d.id)}>−</button>
                    <span>{qty}</span>
                    <button onClick={() => cart.add(restaurant, { id: d.id, name: d.name, price: d.price, veg: d.veg })}>+</button>
                  </div>
                )}
              </div>
            </div>
          );
        })}
      </div>

      {cartHere && (
        <Link to="/cart" className="cart-bar">
          <span>
            {itemCount(cart.items)} item{itemCount(cart.items) > 1 ? 's' : ''} | {rupees(itemsTotal(cart.items))}
          </span>
          <strong>View cart →</strong>
        </Link>
      )}
    </div>
  );
}
