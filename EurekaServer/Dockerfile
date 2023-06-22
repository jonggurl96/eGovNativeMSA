FROM eclipse-temurin:8
EXPOSE 8761
ADD ./target/EurekaServer-0.0.1-SNAPSHOT.jar eureka.jar
ENTRYPOINT ["java", "-jar", "eureka.jar"]
