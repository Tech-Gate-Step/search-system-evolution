# Search System Evolution 
[![Java](https://img.shields.io/badge/Java-21-b07219.svg)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6db33f.svg)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-02303a.svg)](https://gradle.org)
---

**`RDBMS` → `Redis` → `Elasticsearch`**  
**“왜 대규모 이커머스/대용량 트래픽 환경에서 `Elasticsearch`인가?”** 를 입증하기 위한 실습·비교 프로젝트

---

## 🎯 프로젝트 목적
오늘날 대규모 이커머스 플랫폼(예: `쿠팡`, `아마존`)에서는 **수천만**~**수억** 개의 상품 데이터와  
초당 수천 건 이상의 검색 요청이 동시에 발생합니다.

이 프로젝트는 단순히 기술 학습이 아니라,

**문제를 `정의` → `기존 기술 적용` → `한계 검증` → `새로운 해법 적용`**

이라는 과정을 통해 **“왜 대규모 트래픽 환경에서 `Elasticsearch`가 선택되는가?”** 를  
실험과 수치로 입증하는 데 목적이 있습니다.

---

## 🧩 문제 정의
- **시나리오**: 수천만 개의 상품 데이터에서 다음 요구사항을 만족해야 한다.
    - **상품명/카테고리/브랜드/설명에서 키워드 기반 검색**
    - **가격 범위 / 평점 / 카테고리 등 필드별 필터링**
    - **판매량/리뷰수 기반 정렬 및 집계 (예: Best Seller Top 10)**
    - **오타 교정, 연관 검색어 추천**
- **성공지표**
    - **P95 응답시간 ≤ 200ms**
    - **초당 3,000건 이상의 검색 요청 처리(QPS)**
    - **인덱싱 지연 ≤ 10초 (상품 등록 후 검색 반영 시간)**

---

## 🛠️ 사용 기술
### Step 1: RDBMS (Baseline)
- **MySQL / PostgreSQL**
- 인덱스(BTree, Composite), 실행 계획(EXPLAIN), LIKE/Full Text Search
- **한계**: 상품 데이터가 수천만 건 이상으로 커질 경우 검색 속도 급격히 저하, 복잡한 필터 조합 처리에 제약, 수평 확장 어려움

### Step 2: Redis (Cache Layer)
- **Redis + Spring Data Redis**
- Cache-Aside 패턴 적용 (Key-Value 캐싱)
- 베스트셀러, 최근 본 상품, 특정 인기 키워드 검색 결과를 캐싱하여 성능 개선
- **한계**: 캐시에 없는 키워드나 조합 검색은 DB에 의존, Full-text search 불가능, 오타/연관 검색 불가

### Step 3: Elasticsearch
- **Elasticsearch + Spring Data Elasticsearch**
- Lucene 기반 역색인(Inverted Index)
- 형태소 분석, N-gram, 오타 교정, 연관어 추천
- Aggregation으로 가격대별 분포/Top 카테고리/판매량 집계 가능
- 수평 확장(클러스터링) 용이
- **결과**: 대규모 상품 검색/추천/필터링 문제를 근본적으로 해결

---

## 📊 실험/비교 방법
1. 동일한 **상품 데이터셋** 사용 (`datasets/`에 생성 스크립트 포함)
    - 예: `상품ID, 이름, 카테고리, 가격, 평점, 판매량, 리뷰수, 설명`
2. 동일한 **검색 요구사항** 적용
3. 동일한 **벤치마크 도구** 사용 (`wrk`, `redis-benchmark`, `JMeter`)
4. 결과는 `benchmarks/`에 CSV/그래프로 정리

--- 

## 📈 실험 결과 요약
| 기술 | 평균 응답속도 | QPS | 장점 | 한계 |
|------|--------------|-----|------|------|
| RDBMS | ~900ms | ~150 | 정합성 보장, 단순 검색 안정적 | 대규모 상품 검색 불리, LIKE/조합 필터 성능 저하 |
| Redis | ~40ms (캐시 적중) | ~3,500 | 인기 상품/베스트셀러 캐싱 강력 | Full-text search 불가, 캐시 무효화 복잡 |
| Elasticsearch | ~110ms | ~2,800 | Full-text 검색, 오타 교정, 집계·정렬·확장성 우수 | 운영 복잡도, 메모리 사용량 큼 |

---

## 📂 프로젝트 구조
```
search-system-evolution/
├── step1_rdbms/ # RDBMS 상품 검색
│ ├── code/
│ └── 01_RDBMS_상품검색.md
├── step2_redis/ # Redis 캐시 적용
│ ├── code/
│ └── 02_Redis_상품검색.md
├── step3_elasticsearch/ # Elasticsearch 최종 해법
│ ├── code/
│ └── 03_ES_상품검색.md
├── datasets/ # 공통 상품 데이터셋
├── benchmarks/ # 벤치마크 결과 (CSV, 그래프)
├── docs/ # 개발환경/의사결정 기록
│ ├── dev-setup.md
│ └── decisions.md
├── summary.md # 세 단계 비교·회고
└── README.md
```

---

## 📌 협업 및 브랜치 전략
일반적인 팀 개발에서는 `git flow` 또는 `main/develop` 기반의 브랜치 전략을 사용합니다.  
그러나 본 프로젝트는 **스터디 성격**을 가지고 있으며, 각 스터디원이 **서로 다른 패키지/디렉토리 내에서 독립적으로 작업**하기 때문에 충돌 발생 가능성이 거의 없습니다.

따라서 본 프로젝트는 **단일 브랜치(main)** 전략을 채택했습니다.
- 모든 작업은 `main` 브랜치에서 직접 진행합니다.
- 패키지 단위로 코드가 분리되어 있어 충돌 가능성이 낮습니다.
- 불필요한 브랜치 관리 오버헤드를 줄이고, 학습과 비교 실험에 집중할 수 있습니다.

이 방식은 **실무용 협업 프로젝트**에는 적합하지 않을 수 있으나,  
**연구/학습 목적의 실험 프로젝트**에서는 단순성과 효율성을 높여줍니다.

---

## 💡 주요 고민 포인트
- **RDBMS**: 인덱스를 어디까지 최적화하면 버틸 수 있을까? 조합 필터에 대한 한계는?
- **Redis**: 캐시 적중률이 낮은 경우 성능은 어떻게 되는가? 무효화 정책은 어떻게 가져가야 하는가?
- **Elasticsearch**: 역색인 구조가 복잡 필터+정렬 시 실제로 얼마나 효과적인가? 운영 비용은 어떻게 줄일 수 있는가?

---

## 🚀 배운 점
- 단순히 “데이터가 많으니 ES 쓰자”가 아니라, **RDBMS/Redis/ES 각각의 역할과 한계를 수치로 증명**하는 것이 중요하다.
- 이커머스 검색이라는 도메인에서 Redis는 **핫 데이터 캐싱**에 강점이 있지만, 검색 품질을 해결할 수는 없음을 확인했다.
- Elasticsearch는 **검색 품질 + 확장성**을 동시에 제공하지만 운영 난이도가 따른다.
- 결과적으로 “**RDBMS + Redis + Elasticsearch**를 상황에 맞게 조합하는 하이브리드 아키텍처”가 최적임을 알게 됐다.

---

## ▶️ 실행 방법
1. 개발환경 준비: [docs/dev-setup.md](docs/dev-setup.md)
2. 데이터 적재
   ```bash
   make dataset
   make load:rdbms
   make load:redis
   make load:es




