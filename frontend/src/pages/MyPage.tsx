import { useEffect, useState } from 'react';

export function MyPage() {
  const [profile, setProfile] = useState<any>(null);

  useEffect(() => {
    const userId = localStorage.getItem('userId');
    if (!userId) return;
    fetch('http://localhost:8080/api/v1/users/me', { headers: { 'X-User-Id': userId } })
      .then((res) => res.json())
      .then(setProfile);
  }, []);

  return (
    <section>
      <h2>마이페이지</h2>
      {!profile && <p>로그인 후 사용자 정보가 표시됩니다.</p>}
      {profile && <pre>{JSON.stringify(profile, null, 2)}</pre>}
    </section>
  );
}
