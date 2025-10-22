import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '2m', target: 10 },
        { duration: '5m', target: 10 },
        { duration: '2m', target: 0 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<600'],
        http_req_failed: ['rate<0.01'],
    },
};

const keywords = ['LG', 'MSI', '나이키', '삼성', '샤오미', '소니', '아디다스', '애플', '현대'];
const MAX_RESULT_WINDOW = 10000; // Elasticsearch 페이징 제한

// 랜덤 키워드
function getRandomKeyword() {
    return keywords[Math.floor(Math.random() * keywords.length)];
}

function getRealisticPageSize() {
    const rand = Math.random();
    if (rand < 0.80) return 20;        // 80%는 20개
    if (rand < 0.95) return 50;        // 15%는 50개
    return 10;                          // 5%는 10개
}

// 현실적인 페이지 분포 + ES 페이징 제한 적용
function getRealisticPage(pageSize) {
    // ES 제한: from + size <= MAX_RESULT_WINDOW
    // from = page * pageSize, size = pageSize
    // 따라서: page * pageSize + pageSize <= MAX_RESULT_WINDOW
    // page <= (MAX_RESULT_WINDOW - pageSize) / pageSize
    const maxPage = Math.floor((MAX_RESULT_WINDOW - pageSize) / pageSize);

    const rand = Math.random();
    if (rand < 0.70) {
        // 70%는 0~4 페이지
        return Math.floor(Math.random() * Math.min(5, maxPage + 1));
    }
    if (rand < 0.90) {
        // 20%는 0~19 페이지
        return Math.floor(Math.random() * Math.min(20, maxPage + 1));
    }
    // 10%는 0~99 페이지 (단, maxPage 초과 불가)
    return Math.floor(Math.random() * Math.min(100, maxPage + 1));
}

export default function () {
    const keyword = getRandomKeyword();
    const pageSize = getRealisticPageSize();
    const page = getRealisticPage(pageSize); // pageSize를 인자로 전달

    const url = `http://localhost:8080/search/products/list?keyword=${encodeURIComponent(keyword)}&page=${page}&pageSize=${pageSize}`;

    const res = http.get(url);

    const success = check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 600ms': (r) => r.timings.duration < 600,
    });

    // 샘플 로깅
    if (Math.random() < 0.05) {  // 5%만 로깅
        console.log(`[${res.status}] "${keyword}" p${page} s${pageSize} (from=${page * pageSize}) - ${res.timings.duration.toFixed(0)}ms`);
    }

    sleep(Math.random() * 2 + 1);  // 1~3초 랜덤 대기
}