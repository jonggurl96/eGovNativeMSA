# eGovFramework Cloud Native MSA 적용 개발 가이드 따라하기

<img src="https://img.shields.io/badge/Java JDK eclipse temurin 1.8-FFFFFF?style=flat-square&logo=OpenJDK&logoColor=black">
<img src="https://img.shields.io/badge/Spring Boot v2.2.6.RELEASE-6DB33F?style=flat-square&logo=Spring Boot&logoColor=white">
<img src="https://img.shields.io/badge/Spring Cloud v2.2.5.RELEASE-6DB33F?style=flat-square&logo=iCloud&logoColor=white">
<img src="https://img.shields.io/badge/Docker Desktop v4.20.1-2496ED?style=flat-square&logo=Docker&logoColor=white">
<img src="https://img.shields.io/badge/Docker Compose v2.18.1-2496ED?style=flat-square&logo=Docker&logoColor=white">

# Service Mesh
- [화면 서비스: CatalogsService](#catalogsservice)
- [실제 정보를 담고 있는 서비스: CustomersService](#customersservice)
- [Catalogs & Customers 서비스 연동 및 테스트](#catalogs--customers-서비스-연동-및-테스트)
- [Spring Cloud 컴포넌트 활용](#spring-cloud-컴포넌트-활용)
- [Microservice 배포](#microservice-배포)



# CatalogsService
- [Spring Boot Starter Project Catalogs 생성](#spring-boot-starter-project-catalogs-생성)
- [pom.xml 의존성 추가](#pomxml-의존성-추가)
- [apllication.yml 파일 생성](#applicationyml-파일-생성)
- [각 클래스 파일 작성](#각-클래스-파일-작성)
- [Catalogs Service URL](#catalogs-service-url)

## Spring Boot Starter Project Catalogs 생성
- Maven build
- Group: egovframework.msa.sample
- Packaging: Jar
- JDK 8, Java 8
- Spring Boot Version 2.7.12: pom.xml에서 2.2.6.RELEASE로 수정

## pom.xml 의존성 추가
- [pom.xml](Catalogs/pom.xml)

## application.yml 파일 생성
- [application.yml](Catalogs/src/main/resources/application.yml)
- application.properties 파일의 형식 재정의로 yaml 파일로 변경 가능

## 각 클래스 파일 작성
- [CatalogApplication](Catalogs/src/main/java/egovframework/msa/sample/catalogs/CatalogsApplication.java)
- [CustomerApiService](Catalogs/src/main/java/egovframework/msa/sample/service/CustomerApiService.java)
- [CustomerApiServiceImpl](Catalogs/src/main/java/egovframework/msa/sample/serviceImpl/CustomerApiServiceImpl.java)
- [CatalogsController](Catalogs/src/main/java/egovframework/msa/sample/controller/CatalogsController.java)

## Catalogs Service URL
- **http://localhost:8081/catalogs/1234**

# CustomersService
- [Spring Boot Starter Project Customers 생성](#spring-boot-starter-project-customers-생성)
- [application.yml 파일 생성](#applicationyml-파일-생성-1)
- [Customers Service URL](#customers-service-url)

## Spring Boot Starter Project Customers 생성
Catalogs와 동일한 방법으로 생성 후 pom.xml 수정

## application.yml 파일 생성
- [application.yml](Customers/src/main/resources/application.yml)

## 각 클래스 파일 작성
- CustomersApplication에 @ComponentScan annotation 추가
- [CustomerController](Customers/src/main/java/egovframework/msa/sample/controller/CustomerController.java)

## Customers Service URL
- **http://localhost:8082/customers/1234**




# Catalogs & Customers 서비스 연동 및 테스트
> Catalogs 서비스가 Customers 서비스를 호출하므로
> Customers 서비스는 변동이 없고    
> Catalogs 서비스에 RestTemplate을 적용하여 JSON 형태의 결과를 받도록 수정한다.

- [CatalogsApplication](Catalogs/src/main/java/egovframework/msa/sample/catalogs/CatalogsApplication.java)
  - RestTemplate Bean 추가
- [CustomerApiServiceImpl](Catalogs/src/main/java/egovframework/msa/sample/serviceImpl/CustomerApiServiceImpl.java)
  - RestTemplate Bean으로부터 CustomerApi 호출 결과를 반환하도록 수정
  - > return restTemplate.getForObject("url", Class<?> responseType);
- Catalogs & Customers 서비스를 모두 구동하고 테스트 URL 호출
  - > http://localhost:8081/catalogs/customerinfo/1234




# Spring Cloud 컴포넌트 활용
## 적용 Cloud 컴포넌트
- [Circuit Breaker - Hystrix](#circuit-breaker---hystrix)
- [Client LoadBalancer - Ribbon](#client-loadbalancer---ribbon)
- [Service Registry - Eureka](#service-registry---eureka)
- [API Gateway - Zuul](#api-gateway---zuul)
- [Config Server](#config-server)
- [Config Client](#config-client)
- [Spring Cloud Bus - RabbitMQ](#spring-cloud-bus-환경-구성---rabbitmq)
- [Polygot Support - Sidecar](#polygot-support---sidecar)
- [Centralized Logging - ELK](#centralized-logging---elk)
- [Centralized Metrics - Prometheus, Grafana](#centralized-metrics---prometheus-grafana)
- [Distributed Tracing - Sleuth, Zipkin](#distributed-tracing---sleuth-zipkin)

### Circuit Breaker - Hystrix
    분산환경을 위한 장애 및 지연 내성을 갖도록 도와주는 라이브러리
    장애 전파를 방지하기 위해 필수적인 컴포넌트
- 장애 및 지연 내성
- 실시간 구동 모니터링
- 병행성
#### 라이브러리 적용
    서비스를 호출하는 서비스에 적용
> Catalogs 서비스에 Hystrix 적용하기
> - pom.xml
> ```xml
> <dependency>
>   <groupId>org.springframework.cloud</groupId>
>   <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
>   <version>${spring.cloud.version}</version>
> </dependency>
> ```
> - CatalogsApplication.java에 @EnableCircuitBreaker Annotation 추가
> - CustomerApiServiceImpl의 getCustomerDetail() 메서드에 @HystrixCommand(fallbackMethod) Annotation을 추가하고 Fallback 메소드를 추가한다.
> ```java
> public String fallbackMethod(String customerId, Throwable ex) {
>   System.out.println("Error: " + ex.getMessage());
>   return "고객정보 조회가 지연되고 있습니다.";
> }
> ```
> - 원활한 테스트를 위해 CustomersService CustomerController의 getCustomerDetail()메서드에 throw new Exception 구문을 추가하고 실행

### Client LoadBalancer - Ribbon
#### LoadBalancing 
> 부하 분산 처리
> - Scale-Up
>   - 서버 자체의 성능을 확장
> - Scale-Out
>   - 서버 증설
#### Ribbon
> 클라이언트에 탑재할 수 있는 S/W 기반의 Load Balancer   
> 요청을 순서대로 배정하는 Round-Robin 방식으로 부하 분산 기능 제공
- 구성요소
  - Rule: 요청을 보낼 서버를 선택하는 규칙
    - RoundRobin(default): 한 서버씩 돌아가며 전달
    - Available Filtering: 에러가 많은 서버 제외
    - Weighted Response Time: 서버별 응답 시간에 따라 조절
  - Ping: 서버가 살아있는지 확인
    - Static/Dynamic 모두 사용 가능
  - ServerList: 로드 밸런싱 대상 서버 목록
    - Configuration을 통한 정적 설정
    - Eureka를 통한 동적 설정

#### Ribbon 라이브러리 활용
> - Ribbon을 각 서비스를 호출하는 Catalogs 서비스에 적용
> - Ribbon의 @LoadBalanced를 RestTemplate에 적용
   
> - pom.xml
> ```xml
> <dependency>
>   <groupId>org.springframework.cloud</groupId>
>   <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
>   <version>${spring.cloud.version}</version>
> </dependency>
> ```
> - CatalogsApplication의 RestTemplate Bean에 @LoadBalanced Annotation 추가
> - CustomerApiServiceImpl의 Customers 서비스 호출 URL 주소의 localhost:8082를 customer로 변경
> - application.yml에 Ribbon 설정을 추가한다.
> ```yaml
> customer:
>   ribbon:
>     listOfServers: localhost:8082
> ```

### Service Registry - Eureka
> MSA의 장점 중 하나인 동적인 서비스 증설 및 축소를 위한 필수 라이브러리
> - Eureka Server: Eureka Client에 해당하는 Micro Service들의 상태 정보가 등록되어있는 레지스트리 서버
> - Eureka Client: 서비스가 시작될 때 Eureka Server에 자신의 정보를 등록하고 주기적으로 가용상태(health check)를 알리며 
> ping이 확인되지 않으면 Eureka Server에서 해당 서비스를 제외
> 
> Ribbon과 결합하여 서버 목록을 자동으로 관리 및 갱신한다.

#### Eureka Server Service 작성
- Spring Boot Initializr 사용
- 서비스 레지스트리 외의 작업이 없으므로 egov 라이브러리를 제거하고 Eureka 서버 라이브러리만 등록
- EurekaServerApplication에 @EnableEurekaServer Annotation 추가
- application.yml 작성
  ```yaml
  server:
    port:8761
  
  spring:
    application:
      name: EurekaServer
  ```
- 실행 후 localhost:8761로 접속하면 EurekaServer 화면이 나타난다.

#### Eureka Client Service 작성
> 기존의 Catalogs 서비스와 Customers 서비스를 Eureka Client로 설정
##### Catalogs Service와 CustomersService에 Eureka Client 라이브러리 적용
> pom.xml
> CatalogsApplication에 @EnableEurekaClient Annotation 추가
> application.yml에 아래 항목 추가
> ```yaml
> eureka:
>   instance:
>     prefer-ip-address: true
>   client:
>     service-url:
>       default-zone: http://localhost:8761/eureka
> ```
> > CatalogsService의 application.yml에서 Ribbon 관련 부분은 주석처리한다.    
> 실행 후 모든 서비스가 Eureka Server에 등록된다.

### API Gateway - Zuul
> - 모든 클라이언트 요청에 대한 end-point를 통합하는 서비스
> - Eureka, Hystrix, Ribbon 등의 여러 기능을 내장
#### 주요 기능
- 인증 및 보안
- 모니터링
- 동적 라우팅
- 부하 테스트
- 트래픽 드랍
- 정적 응답 처리
#### ZuulService 작성
- 위와 마찬가지의 Spring boot 프로젝트 ZuulServer 작성
- API Gateway 역할 이외의 별도 작업이 없으므로 Zuul, Eureka Client, Spring-retry 라이브러리 등록
  - Zuul
    ```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
        <version>2.2.5.RELEASE</version>
    </dependency>
    ```
  - Eureka Client
    ```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        <version>2.2.5.RELEASE</version>
    </dependency>
    ```
  - Spring-retry
    ```xml
    <dependency>
        <groupId>org.springframework.retry</groupId>
        <artifactId>spring-retry</artifactId>
    </dependency>
    ```
- ZuulServerApplication 작성
  - @EurekaDiscoveryClient Annotation: Eureka, Consul, Zookeeper에도 동작 가능
  - @EnableZuulProxy
- application.yml
  ```yaml
  spring:
    application:
      name: zuul
  
  server:
    port: 8080
  
  zuul:
    routes:
      catalog:
        path: /catalogs/**
        serviceId: catalog
        stripPrefix: false
      customer:
        path: /customers/**
        serviceId: customer
  
  eureka:
    instance:
      non-secure-port: ${server.port}
      prefer-ip-address: true
    client:
      service-url:
        defaultZone: http://localhost:8761/eureka
  ```

### Config Server
> - 분산시스템에서 애플리케이션의 환경설정정보들을 애플리케이션과 분리해 외부에서 관리하도록 한 환경설정 서버
> - DB 접속 정보, 미들웨어 접속정보, 애플리케이션을 구성하는 각종 메타데이터들을 관리
> - Config 서버 구동 시 Environment Repository에서 설정 내용을 내려받고 애플리케이션이 초기화되고 구동된다.
> - 서비스 운영 중 설정 파일 변경 시 Spring Clud Bus를 이용하여 모든 마이크로 서비스의 환경설정을 업데이트할 수 있다.
> - Spring Cloud Bus는 RabbitMQ, Kafka 같은 경량 메시지 브로커를 사용한다.

#### Config 서버 작성
- ConfigServer 프로젝트 생성 후 라이브러리 등록
  - Config Server
    ```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
        <version>2.2.5.RELEASE</version>
    </dependency>
    ```
  - Security와 Actuator
    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    ```
  - Web
    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    ```
- ConfigServerApplication에 @EnableConfigServer Annotation 추가
- application.yml 파일 작성
  ```yaml
  server:
    port: 8888
  
  spring:
    application:
      name:
        -templateSimple
        -templatePortal
        -templateEnterprise
    profiles:
      active: native
    cloud:
      config:
        server:
          native:
            search-locations: classpath:/configuration-repository/ # 검색할 설정파일 경로
  ```
- 환경파일 작성
  - templateSimple-dev.yml
    ```yaml
    config:
      profile: sht
      message: templateSimple(dev)
    
    Globals:
      DbType: mysql
      DriverClassName: com.mysql.jdbc.Driver
      Url: jdbc:mysql://localhost:3306/sht
      Username: com
      Password: com01
    ```
  - templatePortal-dev.yml
    ```yaml
    config:
      profile: pst
      message: templatePortal(dev)
    
    Globals:
      DbType: mysql
      DriverClassName: com.mysql.jdbc.Driver
      Url: jdbc:mysql://localhost:3306/pst
      Username: com
      Password: com01
    ```
  - templateEnterprise-dev.yml
    ```yaml
    config:
      profile: ebt
      message: templateEnterprise(dev)
    
    Globals:
      DbType: mysql
      DriverClassName: com.mysql.jdbc.Driver
      Url: jdbc:mysql://localhost:3306/ebt
      Username: com
      Password: com01
    ```
- 서버 구동 후 localhost:8888/actuator에 접속하여 테스트

### Config Client
- ConfigClient 프로젝트 생성하되 Springboot 3.1.0 버전과 Spring Initializr의 dependency 추가 기능을 사용해 다음 라이브러리를 추가한다.
  - spring boot starter web
  - spring boot starter actuator
  - spring cloud starter config
- ConfigClientApplication에 profile 변경 코드를 추가한다.
  ```yaml
  String profile = System.getProperty("spring.profiles.active");
  if(profile == null) {
      System.setProperty("spring.profiles.acive", "dev");
  }
  ```
- [ConfigClientController]()를 작성한다.
- bootstrap.yml을 작성한다.
  ```yaml
  server:
    port: 9265
  spring:
    application:
      name: templateEnterprise
    cloud:
      config:
        uri: http://localhost:8888 # config server url
  ```
- application.yml을 작성한다.
  ```yaml
  management:
    endpoints:
      web:
        exposure:
          include: ['env', 'refresh'] # endpoint,  ex) http://localhost:9265/actuator/refresh
  ```
- Config Server와 Config Client를 실행하고 아래 URL의 내용이 정상적으로 실행되는지 확인한다.
  - http://localhost:9265/config/profile
  - http://localhost:9265/config/message
  - http://localhost:9265/actuator/env
- 클라이언트 환경설정값 업데이트 테스트
  - ConfigServer의 templateEnterprise-dev.yml의 config.profile 값을 ebt에서 ebt2로 변경한다.
  - rest-api 테스트도구를 통해 /actuator/refresh로 POST 요청을 보낸다.
  - request body에는 "config.profile" 값을 준다.
  - 다시 /config/profile로 접속해 변경된 값이 출력되는지 확인한다.
  - ConfigServer의 application.yml 파일에서 classpath의 templateEnterprise-dev.yml 파일을 바라보게 함
  - > target/classes/configuration-repository/templateEnterprise-dev.yml 파일을 수정해야 원하는 결과가 나옴

### Spring Cloud Bus 환경 구성 - RabbitMQ
> - Config Server 설정값이 변경괼 경우 마이크로 서비스들이 변경된 설정 값을 갱신하기 위해 /actuator/refersh를 수행해줘야한다.
> - Config Server에서 마이크로 서비스들에게 refresh를 수행하라는 메시지를 전송해주는 컴포넌트를 **메시지 브로커**라고 한다.
> - RabbitMQ, Kafka 등이 그 예이다.

#### RabbitMQ on Docker Container
```shell
docker run -d -e TZ=Asia/Seoul --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:management
```

RabbitMQ 관리 URL: localhost:15672
- ID: guest
- PW: guest

#### RabbitMQ 사용을 위한 Config Client 수정
- pom.xml
```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-bus-amqp</artifactId>
  <version>2.2.3.RELEASE</version>
</dependency>
```
- application.yml
> RabbitMQ의 정보 입력 후 엔드포인트를 "*"로 변경
```yaml
spring:
  rabbitmq:
    host: localhost # rabbitmq 호스트
    port: 5672      # rabbitmq 서비스 포트
    username: guest # rabbitmq 사용자명
    password: guest # rabbitmq 비밀번호
```
- Config Server와 Config Client 실행 후 RabbitMQ 관리페이지에 Connection 추가됨
- Exchanges 탭에서 springCloudBus 확인 가능
#### Spring Cloud Bus 테스트
- Config Server의 templateEnterprise-dev.yml 파일의 config.profile 값을 ebt-change로 변경
- classpath의 templateEnterprise-dev.yml 수정 후 Config Client의 bus-refresh actuator를 호출 (POST)
- RabbitMQ가 자동으로 다른 클라이언트들에 refresh 실행
- 테스트 URL localhost:9625/config/profile 접속하면 변경된 속성값이 출력됨

### Polygot Support - Sidecar
> Spring 기반 애플리케이션 외의 비 JVM 애플리케이션을 Spring Cloud와 연동하려면 Spring Cloud Sidecar를 사용한다.
> - Springboot로 작성된 Sidecar를 Eureka에  등록해 연동한다.
> - non-JVM microservice는 Sidecar의 존재를 알지 못한다.
> - API Gateway Zuul을 통해 request를 받고 Ribbon을 통해 로드밸런싱이 기능하며 Config 서버로 환경설정이 가능하다.

#### Sidecar 작성
- pom.xml
  ```xml
  <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-netflix-sidecar</artifactId>
    <version>2.2.5.RELEASE</version>
  </dependency>
  ```
- SidecarApplication에 @EnableSidecar Annotation 추가
  - @EnableSidecar는 내부적으로 @EnableCircuitBreaker, @EnableDiscoveryClient, @EnableZuulProxy를 포함한다.
- application.yml 파일 작성
  ```yaml
  server:
    port: 5678
  spring:
    application:
      name: sidecar-nonJVM
  sidecar:
    port:5000
    health-uri: http://localhost:5000/health.json
    hostname: localhost # sidecar와 비JVM 애플리케이션의 호스트는 동일함
  ```
- 비 JVM 애플리케이션에서 상태체크 Endpoint 작성
> pip install Flask 실행 후 [app.py](app.py) 실행
- Eureka Server, non-JVM service, Sidecar를 실행하면 Eureka Server에 SIDECAR-NONJVM이 등록된다.

### Centralized Logging - ELK
> - Elastic search: Lucene 검색 엔진 기반의 NoSQL DB
> - Logstash: Log Pipeline 도구
> - Kibana: Elasticsearch UI
> - qns산 환경에서 각각의 마이크로서비스에서 생성된 로그들을 한 곳으로 취합
> - Logstash에서 로그 수집, Elastic search로 전송, Kibana 웹사이트에서 로그 확인

#### Docker로 ELK 한 번에 사용하기
~~Docker 안쓰니까 에러를 어떻게 고쳐야 할지 도저히 답이 안나온다~~
```shell
cd elk
docker-compose up -d
```
#### Kibana에서 index 생성 후 로그 확인
- Management 탭 > Index Pattern 추가
- Index Pattern: logstash-*
- Time Field: @timestamp
- Discover 메뉴에서 logstash-* 로그 확인 가능

### Centralized Metrics - Prometheus, Grafana
~~귀찮으니 생략~~

### Distributed Tracing - Sleuth, Zipkin
> 분산 환경 서비스 간 병목 현상 발생 시 분산 환경 로그 트레이싱 필요

#### Docker로 Zipkin 실행
```shell
docker run --name zipkin -d -p 9411:9411 -e TZ=Asia/Seoul openzipkin/zipkin
```

#### 마이크로 서비스 수정
- pom.xml dependency 추가
```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>
```
- application.yml zipkin baseurl 추가
```yaml
spring:
  zipkin:
    base-url: http://localhost:9411/
```




# Microservice 배포

## Spring Boot 애플리케이션 도커 이미지 변환

### 도커라이징
> Dockerfile을 통해 도커 이미지를 생성   
> - Dockerfile: 사용자가 이미지를 조립하기 위해 명령줄에서 호출할 수 있는 모든 명령을 포함하는 텍스트 문서

### Dockerfile
- Dockerfile 구조
```dockerfile
# comment
INSTRUCTION arguments...
```

#### 주요 명령어
| 명령어                       | 설명                                     |
|---------------------------|----------------------------------------|
| [FROM](#from)             | 기본 이미지 지정                              |
| [LABEL](#label)           | 라벨 설명                                  |
| [VOLUME](#volume)         | 볼륨 마운트                                 |
| [EXPOSE](#expose)         | 컨테이너가 리스닝할 포트 및 프로토콜 설정                |
| [ARG](#arg)               | 빌드 시 넘어올 수 있는 전달인자 설정                  |
| [ADD](#addcopy)           | 파일 및 디렉토리 추가                           |
| [ENTRYPOINT](#entrypoint) | 이미지를 컨테이너로 인스턴스화할 때 항상 실행되어야 하는 커맨드 설정 |

###### FROM
```dockerfile
FROM <이미지>
FROM <이미지>:<태그>
```
> 새 빌드 단계를 초기화하고 후속 명령어를 위해 기본 이미지를 설정하므로 
> 모든 Dockerfile은 FROM 명령어로 시작한다. (*ARG 제외*)

###### LABEL
```dockerfile
LABEL <K>=<V>
```
> 이미지에 메타데이터를 추가하며 키-값 쌍으로 구성되며 
> 공백을 포함하려면 따옴표와 백 슬래시를 사용한다.

###### VOLUME
```dockerfile
VOLUME <PATH>
```
> 지정된 이름으로 마운트 지점을 만들고 기본 호스트 또는 다른 컨테이너에서 
> 외부 마운트 된 볼륨을 보유하는 것으로 표시한다.

###### EXPOSE
```dockerfile
EXPOSE <PORT>
EXPOSE <PORT>/<PROTOCOL>
```
> 컨테이너가 런타임에 수신 대기할 네트워크 포트를 지정한다.   
> 프로토콜 기본값은 TCP이다.

###### ARG
```dockerfile
ARG <이름>
ARG <이름>=<기본값>
```
> docker build 명령으로 이미지 빌드 시 --build-arg 옵션을 통해 전달할 인자를 정의한다.

###### ADD/COPY
```dockerfile
ADD [--chown=<user>:<group>] <src>... <dest>
ADD [--chown=<user>:<group>] ["<src>"... "<dest>"]

COPY [--chown=<user>:<group>] <src>... <dest>
COPY [--chown=<user>:<group>] ["<src>"... "<dest>"]
```
> ADD 명령어의 &lt;src&gt;가 압축파일이면 압축 해제하여 추가   
> 로컬 파일을 도커 이미지로 복사하는 경우가 대부분   
- ex)
```dockerfile
# text.txt 복사
COPY text.txt /dest/

# sam으로 시작하는 모든 파일 복사
COPY sam* /dest/

# ? 자리를 다른 문자열로 바꿨을 때 존재하는 파일만 복사
# ex) sample.txt
COPY sampl?.txt /dest/
```

###### ENTRYPOINT
```dockerfile
ENTRYPOINT ["<command>", "<param1>", "<param2>", ...]
ENTRYPOINT <전체 커맨드>
```
> 이미지를 인스턴스화할 때 항상 실행되어야 하는 명령 지정   
> 지정된 명령어를 실행하고 실행된 프로세스가 죽을 때 컨테이너도 같이 종료되므로 
> 도커 이미지를 하나의 실행 파일처럼 사용할 때 유용하다.
- ex)
```dockerfile
# jar 파일을 실행
ENTRYPOINT ["java", "-jar", "catalog.jar"]
```

### 도커 이미지 변환

#### JAR 파일 생성
- eclipse: maven install
- intellij
  - maven: Service-0.0.1-SNAPSHOT.jar
    - 오른쪽의 maven 탭에서 module > lifecycle > package 실행
    - BeanCreationException 발생 시 package task 우클릭 후 실행 구성 수정 우클릭, 실행 탭의 package -f pom.xml에 -DskipTests를 추가한다.
  - gradle
    - 프로젝트 구조 > 아티팩트 > 종속 요소 포함해 jar 추가
    - 모듈과 메인 클래스 적용 후 저장
    - Build > Build Artifacts...
      - jps.track.ap.dependencies 오류
    >     Setting > Build, Execution, Deployment > Compiler > Shared build process VM options에 다음 명령줄을 추가   
      `-Djps.track.ap.dependencies=false`

#### Dockerfile 생성
- 마이크로서비스 폴더에 Dockerfile 생성
- `FROM java:8`: java 8버전 라이브러리를 컨테이너에 추가
- `EXPOSE 8081`: 액세스하기 위한 port 번호 8081
- `ADD ../out/artifacts/Catalogs_jar/Catalogs.jar catalog.jar`: 대상 프로젝트의 JAR 파일을 이미지의 파일 시스템에 추가
- `ENTRYPOINT ["java", "-jar", "catalog.jar"]`: 도커 내에서 JAR 파일을 실행

#### 도커 이미지 변환
> Dockerfile을 만든 디렉토리로 이동해 다음 명령을 실행한다.   
> `docker build -t catalog .`
- docker build <option> <Dockerfile 경로>
- option -t, --tag: 저장소 이름, 이미지 이름, 태그 설정
  - <저장소 이름>/<이미지 이름>:<태그>
> - `docker images`   
> 도커 이미지가 사용 가능한 지 확인   
> - `docker run -d --name catalog-container -p 8081:8081 catalog`   
> 도커 이미지 실행

#### Docker Container에서 동작하기 위한 추가 설정
##### ConfigClient
```yaml
# application.yml
# EurekaServer에 등록
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true

# bootstrap.yml
# ConfigServer가 로딩되고 속성값을 읽을때까지 재시도
spring:
  cloud:
    config:
      fail-fast: true
      retry:
        max-attempts: 1
        initial-interval: 1000
        multiplier: 1.1
```
##### EurekaServer
```yaml
# Eureka Server에 EurekaServer 서비스 제외 및 에러 코드 제거
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

##### ZuulServer
```yaml
eureka:
  instance:
    # non-secure-port: ${server.port}
```

##### docker-compose.yml environments
```yaml
"eureka.client.serviceUrl.defaultZone=http://{등록할 서비스 이름}:{Eureka port}/eureka/"
"spring.zipkin.baseUrl=http://{zipkin 컨테이너 서비스 이름}:{Zipkin port}"
"TZ=Asia/Seoul" # Zipkin, RAbbitMQ TimeZone
"spring.rabbitmq.host={RabbitMQ 서비스 이름}"
"spring.cloud.config.server.native.searchLocations={복사한 설정파일 위치}"
"sidecar.healthUri=http://host.docker.internal:5000/health.json" # host.docker.internal = localhost
```

#### Docker Compose
> Docker Compose를 사용해 전체 시스템 환경을 관리
1. 마이크로서비스를 설명하는 구성 파일 docker-compose.yml 생성
2. [docker-compose.yml](docker-compose.yml)
3. `docker-compose up -d`