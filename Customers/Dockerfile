FROM eclipse-temurin:8
EXPOSE 8082
ADD ./target/Customers-0.0.1-SNAPSHOT.jar customer.jar
ENTRYPOINT ["java", "-jar", "customer.jar"]
