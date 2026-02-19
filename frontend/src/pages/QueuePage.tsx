import { FormEvent, useEffect, useState } from 'react';
import { enrollQueue } from '../api/client';

type QueueState = {
  queueId: string;
  position: number;
  estimatedWaitMinutes: number;
};

export function QueuePage() {
  const [queueId, setQueueId] = useState('hotdeal-seongsimdang');
  const [state, setState] = useState<QueueState>({
    queueId: 'hotdeal-seongsimdang',
    position: 0,
    estimatedWaitMinutes: 0
  });

  const userId = localStorage.getItem('userId') || 'guest';

  const joinQueue = async (e: FormEvent) => {
    e.preventDefault();
    await enrollQueue(queueId, userId);
    setState((prev) => ({ ...prev, queueId }));
  };

  useEffect(() => {
    const eventSource = new EventSource(`http://localhost:8084/api/v1/queue/${queueId}/stream?userId=${userId}`);
    eventSource.onmessage = (event) => {
      const next = JSON.parse(event.data) as QueueState;
      setState(next);
    };
    return () => eventSource.close();
  }, [queueId, userId]);

  return (
    <section>
      <h2>실시간 대기열</h2>
      <form onSubmit={joinQueue} className="row">
        <input value={queueId} onChange={(e) => setQueueId(e.target.value)} />
        <button type="submit">대기열 등록</button>
      </form>
      <div className="card">
        <p>Queue ID: {state.queueId}</p>
        <p>현재 순번: <strong>{state.position}</strong> 번째</p>
        <p>예상 대기 시간: {state.estimatedWaitMinutes}분</p>
      </div>
    </section>
  );
}
