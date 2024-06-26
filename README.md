### 테스트 환경

VisualVM

```
-Xms1024m -Xmx1024m
```

- 비관적 락 O
  <img width="1001" alt="스크린샷 2024-06-29 오전 1 46 03" src="https://github.com/openmpy/springboot-payment/assets/150704638/f7d43ccc-823b-4e00-aa22-ea50c859f2d5">
- 비관적 락 X
  <img width="1001" alt="스크린샷 2024-06-29 오전 1 47 34" src="https://github.com/openmpy/springboot-payment/assets/150704638/e3717bf8-ef0f-4578-a15d-8e1cbdc27a27">

---

1. findTransactionByOrderId 동시에 써질 때 의도대로 동작할까?
    - Transaction이라서 괜찮지 않나?
        - Transaction의 이해 부족
        - 보장 X
    - Update인 경우 Atomic Update로 처리 가능
    - UniqueId 등으로 처리 가능
        - UniqueId는 몇몇 제약이 있어서 원할 때 걸지 못할 수 있다.
        - 이미 중복 가능한 형태로 사용중일지도..
2. 마우스 클릭이 여러번 됐다.
    - 서버에 주문 요청이 3번 들어갔다고 치면 어떻게 중복 주문을 체크할 수 있을까?
    - 동일한 주문/결제 요청이 여러번 들어왔다면 중복 결제를 어떻게 거를 수 있을까?
3. 네트워크 분단
    - 과정
        1. 주문 > 결제 요청
        2. 결제 성공 시 주문 데이터 저장 및 응답
        3. 결제 실패 시 데이터를 저장하지 않고 응답
    - b번 구간에서 네트워크 오류가 발생한다면?
        1. Connect Timeout
        2. Read Timeout
    - 네트워크 오류 발생시 어떻게 대응할건가?
        1. 재시도 한다.
        2. 재결제 한다. > 오류 응답
        3. 기타 등등

---

## 멱등성이란?

Idempotency
여러번 수행해도 동일한 결과를 응답한다.

- 멱등한 API라면 여러번 수행해도 같은 결과를 응답한다.
- 멱등하지 않다면?
    - 중복 요청 처리 된다.
    - 구매를 가정 해보자.
        - Read Timeout이 발생한다.
    - 충전을 가정해보자
    - Open API에서 어떻게 처리 되는지 확인하자.
- 멱등하지 않은 디자인
- 멱등한 디자인
- 실제 문제가 해결되는지 확인
- Read Timeout을 가정함
- 데이터 중복 요청이 Read Timeout에 의해 발생할 수 있다.
- 멱등키로 재시도를 실행하는 것을 문제 해결의 방법중 하나로 사용할 수 있으나, 모든 Read Timeout 상황에서 멱등키 하나로 문제를 해결할 수는 없다.
- 그러나 네트워크 오류, 중복 처리 등을 대응하기 위해 널리 쓰이는 방법

---

결제 충전 등 삽입 시 중복 요청

- 이 부분은 멱등성 키를 통해서 해결
- 동시에 요청이 들어오지 않는 경우에는 잘 동작

동시에 써질 때 아래와 같은 문제 발생

- 여러 트랜잭션에서 다른 트랜잭션의 업데이트가 반영되기 전의 데이터를 읽어버린다.
- 읽기와 쓰기가 동시에 발생할 수 있다면, 이 문제는 기본적인 트랜잭션 모델에서 해결하기 어렵다.
- 이 문제는 대부분의 서비스에 존재한다.
    - 가입 API의 이메일 중복 체크
    - 지갑 생성 API의 이미 존재하는 지갑 확인
    - 충전 API에서의 이미 삽입된 충전 거래 확인
- 위 API처럼 데이터를 삽입하는 거래에서 동시에 요청이 들어올 수 있고, 무결성이 보장되어야 하는 API라면 문제가 심각해진다.

### 해결 방법

- 조회 - 삽입 구간에 여러 처리자가(쓰레드, 트랜잭션) 동시에 접근할 수 없게 만든다.
- 한번에 하나만 처리하게 하던지, 동일한 데이터를 조회할 수 없게 한다.

### 정리

- 동시에 실행되는 요청이 문제 없이 처리되어야 하는데, 일부 케이스에서 무결성을 보장하는데 실패했다.
- Java에서는 동시성 문제를 어떻게 해결?
    - 특정 데이터 구조에 접근하는 전체 메서드 구간을 잠궜다.
    - Synchronized method(block)
    - 서버가 여러대 띄워져 있으면 활용하기 어렵고 성능상 문제가 심각하다.
- MySQL에서는 동시성 문제를 어떻게 해결?
    - Pessimistic_Write Lock
    - Select for update
        - 어떤 기준으로 잠금을 획득할까?
            - Lock의 구현은 DB마다 다를 수 있다.
            - MySQL에서는 Index가 있다면 Index 구조를 잠근다.
                - 일반적으로 레코드 락이라고 표현하면 👍
                - 전체 구조를 잠그지 않고 검색 조건에 일치하는 구간을 잠근다.
            - Index가 없다면 어떻게 잠길까?
                - 생각하기
    - Select * from ... where ... for update
    - 다른 잠금 방법 생각해보기
- Redis에서는 동시성 문제를 어떻게 해결?
    - 동일한 비관적 락 아이디어
    - 그러나 레디스에서도 조회와 쓰기 타이밍이 다르다면 레디스 내에서 동시성 이슈가 발생할 수 있다.
    - Redis의 원자적 연산 기능을 활용해 락을 취득하는 구현을 만들었다.
- 핵심 아이디어는 동일하다.
    - 조회 - 쓰기를 처리하기 전에 잠근다.
    - 나만 사용하고 나간 후에 다른 처리가 진행되도록 한다.
    - 잠그는 범위가 너무 크니 락을 분할한다.
        - Ex) 레코드 락
- 다른 해결 방법은 없을까?
    - 트랜잭션 격리 수준을 조정하는 방법도 있다.
        - 공부하기

---

- 서비스에서 동시성을 제공하기 위해서 이해하기 위해서 DB의 동작을 이해하는 것은 중요하다.
    - 트랜잭션
    - DB 격리 수준
    - Lock
    - Index
- 책 읽어보기
    - Real MySQL, 데이터베이스 중심 어플리케이션 설계, 데이터베이스 인터널스