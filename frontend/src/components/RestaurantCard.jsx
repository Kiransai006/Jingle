import { Link } from 'react-router-dom';
import FoodImage from './FoodImage.jsx';

export default function RestaurantCard({ restaurant: r }) {
  return (
    <Link to={`/restaurant/${r.id}`} className="r-card">
      <div className="r-card-img">
        <FoodImage src={r.imageUrl} alt={r.name} />
        {r.offer && <div className="r-offer">{r.offer}</div>}
      </div>
      <div className="r-card-body">
        <h3>{r.name}</h3>
        <div className="r-meta">
          <span className="rating">★ {r.rating.toFixed(1)}</span>
          <span>·</span>
          <span>{r.deliveryMinutes}-{r.deliveryMinutes + 10} mins</span>
        </div>
        <p className="r-cuisine">{r.cuisine}</p>
        <p className="r-area">{r.area}</p>
      </div>
    </Link>
  );
}
