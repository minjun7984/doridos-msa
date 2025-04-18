# **🏗️모놀리식 구조의 아키텍처를 MSA 구조로 전환하는 프로젝트**
"거대한 하나의 덩어리에서, 유연하고 확장 가능한 마이크로서비스로!"

트랜잭션을 어떻게 관리할 것인가? 장애 전파는 어떻게 막을 것인가?

MSA 전환의 모든 과정과 기술적 고민을 담은 프로젝트입니다.
</br>
</br>
## 🏷 **프로젝트 개요**
기존 모놀리식 아키텍처의 한계를 극복하기 위해 마이크로서비스 아키텍처(MSA)로 전환한 프로젝트입니다.
단순히 서비스를 나누는 것이 아니라, 서비스 간 통신, 트랜잭션 관리, 장애 대응 등
MSA로 전환하며 마주친 실제 문제들을 해결하는 과정에 집중했습니다.

👉 서비스 분리

✔️ Ticket Service – 티켓 생성, 수정, 검색, 조회

✔️ User Service – 사용자 인증 및 회원 관리, 소셜 로그인

✔️ Reservation Service – 티켓 예매 및 상태 관리

✔️ Payment Service – 토스 간편 결제
</br>
</br>

## 🛠 기술 스택

| 분야 | 기술 스택 |
|------|------------------------------------------|
| **언어 & 프레임워크** | Java 17, Spring Boot 3.x |
| **서비스 관리** | Spring Cloud (Eureka, Gateway, Config, OpenFeign) |
| **데이터 관리** | MySQL, Redis, JPA (Hibernate), Query DSL, Kafka, RabbitMQ, Elasticsearch |
| **트랜잭션 관리** | Transactional Outbox |
| **테스트** | Junit5, Mockito |
| **장애 대응** | Circuit Breaker (Resilience4J) |
</br>
</br>

## 🏗️ 인프라
  - 프로젝트는 MSA로 구성되어 있으며 총 7개(유저, 예매, 결제, 티켓, API Gateway, Config, Discovery)의 서비스로 이루어져 있습니다.
  - 데이터베이스는 Mysql, Redis, Elasticsearch를 사용합니다.
  - MessageQueue로는 Kafka, RabbitMQ를 사용합니다.


![구성도](https://github.com/minjun7984/readme-image/blob/main/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202025-02-03%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%205.08.06.png)
</br>
</br>

## 기술적 고민
### 1. **트랜잭션과 이벤트 발행의 분리 문제**
프로젝트에서 결제 처리 후 예약 상태를 업데이트하는 과정에서 **이벤트 발행과 트랜잭션**이 분리되어 있었기 때문에 몇 가지 문제가 발생했습니다. 기존 로직에서는 결제가 성공한 후 이벤트를 발행하게 되는데 만약 트랜잭션이 실패하면 데이터는 롤백되지만, 이미 발행된 이벤트는 그대로 남아 있어 데이터 정합성 문제가 발생할 수 있었습니다.

![문제상황](https://github.com/minjun7984/readme-image/blob/main/%E1%84%90%E1%85%B3%E1%84%85%E1%85%A2%E1%86%AB%E1%84%8C%E1%85%A2%E1%86%A8%E1%84%89%E1%85%A7%E1%86%AB%E1%84%8B%E1%85%A1%E1%84%8B%E1%85%AE%E1%86%BA%E1%84%87%E1%85%A1%E1%86%A8%E1%84%89%E1%85%B3%E1%84%91%E1%85%A2%E1%84%90%E1%85%A5%E1%86%AB%E1%84%8C%E1%85%A5%E1%86%A8%E1%84%8B%E1%85%AD%E1%86%BC%E1%84%8C%E1%85%A5%E1%86%AB.png)

-> 이러한 문제를 해결하기 위해 **Transactional Outbox Pattern**을 적용했습니다.

</br>

### 2. **Transactional Outbox Pattern 선택 이유**
Transactional Outbox Pattern을 선택한 이유는 다음과 같습니다.
- **트랜잭션과 이벤트 발행의 일관성 보장**: 결제 트랜잭션과, 이벤트를 분리하여 관리하는 대신, 하나의 트랜잭션 내에서 처리해 트랜잭션이 롤백되도 이벤트와 결제가 같이 롤백되기 때문에 이벤트 발행의 신뢰성을 보장할 수 있습니다.
- **이벤트 발행의 신뢰성**: 이벤트 발행 실패 시, `Outbox` 테이블을 통해 메시지를 보관하고, 별도로 비동기 처리를 통해 다시 발행할 수 있어, 이벤트 발행의 신뢰성을 확보할 수 있습니다.

### 3. **적용 과정**
1. **Outbox 테이블 도입**: 결제 트랜잭션 내에서 예약 상태 업데이트와 관련된 이벤트 메시지를 `Outbox` 테이블에 저장합니다.
2. **메시지 발행**: 별도의 서비스나 배치 프로세스가 `Outbox` 테이블을 확인하고, 메시지 큐로 이벤트 메시지를 발행합니다.
3. **메시지 처리**: Reservation Service는 해당 메시지를 메시지 큐에서 받아 예약 상태를 업데이트합니다.

![트랜잭션아웃박스](https://github.com/minjun7984/readme-image/blob/main/%E1%84%90%E1%85%B3%E1%84%85%E1%85%A2%E1%86%AB%E1%84%8C%E1%85%A2%E1%86%A8%E1%84%89%E1%85%A7%E1%86%AB%E1%84%8B%E1%85%A1%E1%84%8B%E1%85%AE%E1%86%BA%E1%84%87%E1%85%A1%E1%86%A8%E1%84%89%E1%85%B3.png)

### 4. **결과**
- 결제와 예약 상태 업데이트 이벤트가 하나의 트랜잭션 내에서 처리되어 데이터 정합성 문제를 해결할 수 있었습니다.
- 이벤트 발행 실패 시에도 `Outbox` 테이블에 저장된 메시지를 다시 발행함으로써 신뢰성을 높일 수 있습니다.


</br>



## 구현과정
모놀리식 구조의 애플리케이션을 MSA로 전환하는 과정들을 블로그에 포스팅했습니다.

1. [[MSA 전환하기 1편] 모놀리식 아키텍처와 MSA란 무엇인가](https://alswns7984.tistory.com/106)
2. [[MSA 전환하기 2편] 멀티 모듈 구성하기](https://alswns7984.tistory.com/120)
3. [[MSA 전환하기 3편] Service Discovery 적용하기](https://alswns7984.tistory.com/121)
4. [[MSA 전환하기 4편] Spring Cloud Gateway 구현하기](https://alswns7984.tistory.com/122)
5. [[MSA 전환하기 5편] Spring Cloud Config 도입하기(+Spring Cloud Bus)](https://alswns7984.tistory.com/123)
6. [[MSA 전환하기 6편] OpenFeign을 활용한 서비스 간 통신하기](https://alswns7984.tistory.com/124)
7. [[MSA 전환하기 7편] 서킷 브레이커 적용하기(Resilence4J)](https://alswns7984.tistory.com/125)
8. [[MSA 전환하기 8편] Kafka를 활용한 이벤트 기반 아키텍처 구축하기](https://alswns7984.tistory.com/127)
9. [[MSA 전환하기 9편] Transactional Outbox Pattern을 사용해 분산시스템에서 메시지 발행 신뢰성 보장하기](https://alswns7984.tistory.com/128)

</br>

Elasticsearch를 활용한 검색서비스 구현과정을 블로그에 포스팅했습니다.
1. [Elasticsearch 활용한 검색 서비스 만들기 1편 (Docker로 Elasticsearch + Kibana 구축)](https://alswns7984.tistory.com/130)
2. [Elasticsearch 활용한 검색서비스 만들기 2편 (feat. Spring Boot)](https://alswns7984.tistory.com/131)
3. [Elasticsearch 활용한 검색서비스 만들기 3편 (검색어 자동완성 기능 + 검색 고도화하기)](https://alswns7984.tistory.com/132)

</br>
</br>

## 테스트
- 100개 이상의 테스트코드 작성 

![테스트](https://github.com/minjun7984/readme-image/blob/main/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202024-10-17%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%201.52.09.png)
</br>
</br>


## TODO
- [x] elasticsearch 도입 검색서비스 -> 개발 완료
- [x] 리뷰 기능 개발 -> 개발 완료
- [ ] LLM 기술을 활용한 리뷰 요약 및 키워드 추출
- [x] AWS 배포


