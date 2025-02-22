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

✔️ Ticket Service – 티켓 관리

✔️ User Service – 사용자 인증 및 회원 관리

✔️ Reservation Service – 좌석 예약 및 상태 관리

✔️ Payment Service – 토스 간편 결제
</br>
</br>

## 🛠 기술 스택

| 분야 | 기술 스택 |
|------|------------------------------------------|
| **언어 & 프레임워크** | Java 17, Spring Boot 3.x |
| **서비스 관리** | Spring Cloud (Eureka, Gateway, Config, OpenFeign) |
| **데이터 관리** | MySQL, Redis, JPA (Hibernate), Query DSL, Kafka, RabbitMQ |
| **트랜잭션 관리** | Transactional Outbox |
| **테스트** | Junit5, Mockito |
| **장애 대응** | Circuit Breaker (Resilience4J) |
</br>
</br>

## 배포구성도
![구성도](https://github.com/minjun7984/readme-image/blob/main/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202025-02-03%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%205.08.06.png)
</br>
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
</br>

## 테스트
- 100개 이상의 테스트코드 작성 

![테스트](https://github.com/minjun7984/readme-image/blob/main/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202024-10-17%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%201.52.09.png)
</br>
</br>


