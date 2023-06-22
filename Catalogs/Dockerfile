# JDK Eclipse temurin 1.8
FROM eclipse-temurin:8
EXPOSE 8081
ADD ./target/Catalogs-0.0.1-SNAPSHOT.jar catalog.jar
ENTRYPOINT ["java", "-jar", "catalog.jar"]
