import { Routes, Route } from 'react-router-dom';
import Header from './components/Header.jsx';
import Home from './pages/Home.jsx';
import RestaurantPage from './pages/RestaurantPage.jsx';
import Cart from './pages/Cart.jsx';
import OrderSuccess from './pages/OrderSuccess.jsx';

export default function App() {
  return (
    <>
      <Header />
      <main>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/restaurant/:id" element={<RestaurantPage />} />
          <Route path="/cart" element={<Cart />} />
          <Route path="/order/:id" element={<OrderSuccess />} />
          <Route path="*" element={<div className="container empty">Page not found.</div>} />
        </Routes>
      </main>
      <footer className="footer">
        <div className="container">
          <strong>jingle</strong> · Andhra ruchulu, delivered hot · Demo app for CI/CD &amp; observability
        </div>
      </footer>
    </>
  );
}
