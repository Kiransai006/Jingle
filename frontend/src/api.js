// All calls go to /api on the same origin.
// Dev: Vite proxies /api -> localhost:8080. Docker/K8s: nginx proxies /api -> backend service.
const BASE = import.meta.env.VITE_API_BASE || '/api';

async function request(path, options = {}) {
  const res = await fetch(BASE + path, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });
  if (!res.ok) {
    let message = `Request failed (${res.status})`;
    try {
      const body = await res.json();
      if (body.message) message = body.message;
    } catch {
      /* ignore */
    }
    throw new Error(message);
  }
  return res.json();
}

export const api = {
  restaurants: () => request('/restaurants'),
  restaurant: (id) => request(`/restaurants/${id}`),
  menu: (id) => request(`/restaurants/${id}/menu`),
  placeOrder: (order) => request('/orders', { method: 'POST', body: JSON.stringify(order) }),
  order: (id) => request(`/orders/${id}`),
};
