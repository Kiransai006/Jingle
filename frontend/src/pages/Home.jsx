import { useEffect, useMemo, useState } from 'react';
import { api } from '../api.js';
import RestaurantCard from '../components/RestaurantCard.jsx';
import FoodImage from '../components/FoodImage.jsx';

const IMG = (file) => `https://en.wikipedia.org/wiki/Special:FilePath/${file}?width=320`;

const HERO =
  'https://en.wikipedia.org/wiki/Special:FilePath/Hyderabadi_Biryani_with_Raita,_Mirchi_Ka_Salan_and_Salad.JPG?width=800';

const CATEGORIES = [
  { label: 'Biryani', match: 'biryani', img: IMG('Hyderabadi_Chicken_Biryani.jpg') },
  { label: 'Meals', match: 'meals', img: IMG('Vegetarian_Andhra_Meal.jpg') },
  { label: 'Tiffins', match: 'tiffin', img: IMG('Pesarattu.jpg') },
  { label: 'Spicy', match: 'spicy', img: IMG('Chicken_65_(Dish).jpg') },
  { label: 'Sweets', match: 'sweet', img: IMG('Pootharekulu_dry_fruits.jpg') },
  { label: 'Home Style', match: 'home', img: IMG('Gutti_Vankaya_Kura.jpg') },
];

export default function Home() {
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [category, setCategory] = useState(null);

  useEffect(() => {
    api
      .restaurants()
      .then(setRestaurants)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  const visible = useMemo(() => {
    const q = search.trim().toLowerCase();
    return restaurants.filter((r) => {
      const text = `${r.name} ${r.cuisine} ${r.area}`.toLowerCase();
      return (!q || text.includes(q)) && (!category || text.includes(category));
    });
  }, [restaurants, search, category]);

  return (
    <>
      <section className="hero">
        <div className="container hero-inner">
          <div>
            <h1>
              Andhra ruchulu,
              <br />
              <span>delivered hot.</span>
            </h1>
            <p>Biryani, pesarattu, gongura and more from the best kitchens of Andhra &amp; Telangana.</p>
            <input
              className="search"
              placeholder="Search for restaurants or dishes…"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
          <div className="hero-img">
            <FoodImage src={HERO} alt="Hyderabadi biryani with mirchi ka salan" />
          </div>
        </div>
      </section>

      <section className="container">
        <h2 className="section-title">What's on your mind?</h2>
        <div className="categories">
          {CATEGORIES.map((c) => (
            <button
              key={c.label}
              className={`category ${category === c.match ? 'active' : ''}`}
              onClick={() => setCategory(category === c.match ? null : c.match)}
            >
              <FoodImage src={c.img} alt={c.label} className="category-img" />
              <span>{c.label}</span>
            </button>
          ))}
        </div>

        <h2 className="section-title">
          Restaurants near you <small>{!loading && !error && `${visible.length} places`}</small>
        </h2>

        {loading && <div className="empty">Loading restaurants…</div>}
        {error && (
          <div className="error">
            Couldn't reach the Jingle API ({error}). Is the backend running on port 8080?
          </div>
        )}
        {!loading && !error && visible.length === 0 && <div className="empty">No restaurants match your search.</div>}

        <div className="grid">
          {visible.map((r) => (
            <RestaurantCard key={r.id} restaurant={r} />
          ))}
        </div>
      </section>
    </>
  );
}
