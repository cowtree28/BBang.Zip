import { useEffect, useState } from 'react';

type QueueState = {
  queueId: string;
  position: number;
  estimatedWaitMinutes: number;
};

export function QueuePage() {
  const [state, setState] = useState<QueueState>({
    queueId: 'hotdeal-seongsimdang',
    position: 12,
    estimatedWaitMinutes: 24
  });

  useEffect(() => {
    const eventSource = new EventSource('http://localhost:8084/api/v1/queue/hotdeal-seongsimdang/stream');
    eventSource.onmessage = (event) => {
      const next = JSON.parse(event.data) as QueueState;
      setState(next);
    };

    return () => eventSource.close();
  }, []);

  return (
    <section>
      <h2>실시간 대기열</h2>
      <div className="card">
        <p>Queue ID: {state.queueId}</p>
        <p>현재 순번: <strong>{state.position}</strong> 번째</p>
        <p>예상 대기 시간: {state.estimatedWaitMinutes}분</p>
      </div>
    </section>
  );
}
