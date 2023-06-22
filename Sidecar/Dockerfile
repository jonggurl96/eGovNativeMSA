FROM eclipse-temurin:8
EXPOSE 5678
ADD ./target/Sidecar-0.0.1-SNAPSHOT.jar sidecar.jar
ENTRYPOINT ["java", "-jar", "sidecar.jar"]
