import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 1000 }, // 30초 동안 1000명까지 증가
    { duration: '1m', target: 5000 },  // 1분 동안 5000명까지 유지
    { duration: '30s', target: 0 },    // 30초 동안 서서히 종료
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],   // 에러율 1% 미만 유지 목표
    http_req_duration: ['p(95)<500'], // 95%의 요청은 500ms 이내 응답 목표
  },
};

export default function () {
  // 본인의 AWS IP와 공지사항 조회 엔드포인트로 수정하세요.
  const url = 'http://3.34.126.119:8080/api/notices'; 
  const res = http.get(url);

  check(res, {
    'is status 200': (r) => r.status === 200,
  });

  sleep(1); // 가상 유저당 1초의 대기 시간 (현실적인 유저 행동 반영)
}