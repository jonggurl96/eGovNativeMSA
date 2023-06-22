FROM eclipse-temurin:8
EXPOSE 9265
ADD ./target/ConfigClient-0.0.1-SNAPSHOT.jar configclient.jar
ENTRYPOINT ["java", "-jar", "configclient.jar"]
