const roadmap = [
  '1주차: 도메인 모델링, API 명세 확정, 인프라 초안 작성',
  '2주차: Auth/Product/Order 서비스 MVP 구현',
  '3주차: Queue 서비스 + SSE 실시간 반영 + 결제 연동 Mock',
  '4주차: 프론트 고도화/통합 테스트/배포 자동화'
];

export function RoadmapPage() {
  return (
    <section>
      <h2>개발 로드맵</h2>
      <ol>
        {roadmap.map((item) => (
          <li key={item}>{item}</li>
        ))}
      </ol>
    </section>
  );
}
