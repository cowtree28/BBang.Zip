import { Link, Route, Routes } from 'react-router-dom';
import { HomePage } from './pages/HomePage';
import { QueuePage } from './pages/QueuePage';
import { RoadmapPage } from './pages/RoadmapPage';
import { AuthPage } from './pages/AuthPage';
import { ProductsPage } from './pages/ProductsPage';
import { MyPage } from './pages/MyPage';

export function App() {
  return (
    <div className="layout">
      <header>
        <h1>BBang.Zip</h1>
        <nav>
          <Link to="/">개요</Link>
          <Link to="/auth">회원</Link>
          <Link to="/products">상품</Link>
          <Link to="/queue">대기열</Link>
          <Link to="/mypage">마이페이지</Link>
          <Link to="/roadmap">계획</Link>
        </nav>
      </header>
      <main>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/auth" element={<AuthPage />} />
          <Route path="/products" element={<ProductsPage />} />
          <Route path="/queue" element={<QueuePage />} />
          <Route path="/mypage" element={<MyPage />} />
          <Route path="/roadmap" element={<RoadmapPage />} />
        </Routes>
      </main>
    </div>
  );
}
