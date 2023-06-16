# eGovFramework Cloud Native MSA 적용 개발 가이드 따라하기
- JDK Eclipse temurin 1.8
- eGovframework 4.0.0
- SpringBoot 2.2.6.RELEASE
- Spring Framework 5.2.5
- Spring swagger 2.9.2

# Service Mesh
- [화면 서비스: CatalogsService](#catalogsservice)
- [실제 정보를 담고 있는 서비스: CustomersService](#customersservice)
- [Catalogs & Customers 서비스 연동 및 테스트](#catalogs--customers-서비스-연동-및-테스트)
- [Spring Cloud 컴포넌트 활용](#spring-cloud-컴포넌트-활용)



# CatalogsService
- [Spring Boot Starter Project Catalogs 생성](#spring-boot-starter-project-catalogs-생성)
- [pom.xml 의존성 추가](#pomxml-의존성-추가)
- [apllication.yml 파일 생성](#applicationyml-파일-생성)
- [각 클래스 파일 작성](#각-클래스-파일-작성)
- [Catalogs Service URL](#catalogs-service-url)

## Spring Boot Starter Project Catalogs 생성
- Service URL : https://start.spring.io
- Use Default Location check
- Maven build
- Group: egovframework.msa.sample
- Artifact: Catalogs
- Group Id: egovframework.msa.sample
- Packaging: Jar
- JDK 17, Java 17
   
- Spring Boot Version 3.1.0
- Spring Initializr의 Developer Tools 
  - GraalVM Native Support
  - Spring Boot DevTools
  - Lombok
  - Spring Configuration Processor

## pom.xml 의존성 추가
- [pom.xml](Catalogs/pom.xml)

## application.yml 파일 생성
- [application.yml](Catalogs/src/main/resources/application.yml)
- application.properties 파일의 형식 재정의로 yaml 파일로 변경 가능

## 각 클래스 파일 작성
- [CatalogApplication](Catalogs/src/main/java/egovframework/msa/sample/catalogs/CatalogsApplication.java)
  > @ComponentScan(basePackages = "egovframework") 추가
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

### Config Server






