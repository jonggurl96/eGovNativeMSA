FROM eclipse-temurin:8
EXPOSE 8888
ADD ./target/ConfigServer-0.0.1-SNAPSHOT.jar configserver.jar
COPY ./src/main/resources/configuration-repository /var/conf/server/conf-repo
ENTRYPOINT ["java", "-jar", "configserver.jar"]
