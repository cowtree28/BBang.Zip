import { Link, Route, Routes } from 'react-router-dom';
import { HomePage } from './pages/HomePage';
import { QueuePage } from './pages/QueuePage';
import { RoadmapPage } from './pages/RoadmapPage';

export function App() {
  return (
    <div className="layout">
      <header>
        <h1>BBang.Zip</h1>
        <nav>
          <Link to="/">서비스 개요</Link>
          <Link to="/queue">실시간 대기열</Link>
          <Link to="/roadmap">개발 계획</Link>
        </nav>
      </header>
      <main>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/queue" element={<QueuePage />} />
          <Route path="/roadmap" element={<RoadmapPage />} />
        </Routes>
      </main>
    </div>
  );
}
