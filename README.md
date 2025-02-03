# **모놀리식 구조의 아키텍처를 MSA 구조로 전환하는 프로젝트**

## 🏷 **프로젝트 개요**
기존 Monolithic 구조의 프로젝트를 MSA 구조로 전환한 프로젝트입니다.
프로젝트는 Ticket, User, Reservation, Payment 4개의 서비스로 분리되어 독립적으로 동작하게 됩니다.
MSA로 전환하는 경험에 초점을 맞추고 진행했습니다.

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
</br>
</br>

## 테스트
- 100개 이상의 테스트코드 작성 

![테스트](https://github.com/minjun7984/readme-image/blob/main/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202024-10-17%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%201.52.09.png)
</br>
</br>


