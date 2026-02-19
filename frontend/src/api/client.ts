const AUTH_BASE = 'http://localhost:8081/api/v1';
const PRODUCT_BASE = 'http://localhost:8082/api/v1';
const ORDER_BASE = 'http://localhost:8083/api/v1';
const QUEUE_BASE = 'http://localhost:8084/api/v1';

export async function signUp(payload: Record<string, unknown>) {
  const res = await fetch(`${AUTH_BASE}/auth/signup`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  return res.json();
}

export async function login(username: string, password: string) {
  const res = await fetch(`${AUTH_BASE}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  return res.json();
}

export async function getProducts(keyword = '') {
  const query = keyword ? `?keyword=${encodeURIComponent(keyword)}` : '';
  const res = await fetch(`${PRODUCT_BASE}/products${query}`);
  return res.json();
}

export async function createOrder(tokenUserId: string, payload: Record<string, unknown>) {
  const res = await fetch(`${ORDER_BASE}/orders`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-User-Id': tokenUserId
    },
    body: JSON.stringify(payload)
  });
  return res.json();
}

export async function enrollQueue(queueId: string, userId: string) {
  const res = await fetch(`${QUEUE_BASE}/queue/enroll`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ queueId, userId })
  });
  return res.json();
}
