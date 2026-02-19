import { FormEvent, useState } from 'react';
import { login, signUp } from '../api/client';

export function AuthPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [result, setResult] = useState('');

  const onSignUp = async (e: FormEvent) => {
    e.preventDefault();
    const res = await signUp({
      username,
      password,
      name: '테스트유저',
      birthDate: '2000-01-01',
      gender: 'OTHER',
      accountNumber: '111-222-333',
      region: '대전',
      consent: true
    });
    setResult(JSON.stringify(res));
  };

  const onLogin = async () => {
    const res = await login(username, password);
    localStorage.setItem('userId', res.username || username);
    setResult(JSON.stringify(res));
  };

  return (
    <section>
      <h2>회원가입 / 로그인</h2>
      <form onSubmit={onSignUp} className="card">
        <input placeholder="아이디" value={username} onChange={(e) => setUsername(e.target.value)} />
        <input placeholder="비밀번호" type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        <div className="row">
          <button type="submit">회원가입</button>
          <button type="button" onClick={onLogin}>로그인</button>
        </div>
      </form>
      <pre>{result}</pre>
    </section>
  );
}
