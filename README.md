# eGovFramework Cloud Native MSA 적용 개발 가이드 따라하기
- JDK Eclipse temurin 1.8
- eGovframework 4.0.0
- SpringBoot 3.1.0
- Spring Framework 5.2.5
- Spring swagger 2.9.2

# Service Mesh
- [화면 서비스: CatalogsService](#catalogsservice)
- [실제 정보를 담고 있는 서비스: CustomersService](#customersservice)
- [Catalogs & Customers 서비스 연동 및 테스트](#catalogs--customers-서비스-연동-및-테스트)



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
- [application.yml](Catalogs/src/main/resources/application.yaml)
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














