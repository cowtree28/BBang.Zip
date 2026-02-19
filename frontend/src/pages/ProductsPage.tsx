import { useEffect, useState } from 'react';
import { createOrder, getProducts } from '../api/client';

type Product = { id: number; name: string; description: string; price: number; stock: number; region: string; rating: number };

export function ProductsPage() {
  const [keyword, setKeyword] = useState('');
  const [items, setItems] = useState<Product[]>([]);
  const [message, setMessage] = useState('');

  const load = async () => {
    const res = await getProducts(keyword);
    setItems(res.items || []);
  };

  useEffect(() => {
    load();
  }, []);

  const order = async (product: Product) => {
    const userId = localStorage.getItem('userId') || 'guest';
    const res = await createOrder(userId, {
      productId: product.id,
      productName: product.name,
      quantity: 1,
      priceSnapshot: product.price,
      pickupLocation: product.region
    });
    setMessage(`주문 완료: ${res.orderId}`);
  };

  return (
    <section>
      <h2>빵 목록</h2>
      <div className="row">
        <input placeholder="검색어" value={keyword} onChange={(e) => setKeyword(e.target.value)} />
        <button onClick={load}>검색</button>
      </div>
      {items.map((item) => (
        <article className="card" key={item.id}>
          <h3>{item.name}</h3>
          <p>{item.description}</p>
          <p>{item.price}원 / 재고 {item.stock}</p>
          <button onClick={() => order(item)}>주문하기</button>
        </article>
      ))}
      <p>{message}</p>
    </section>
  );
}
