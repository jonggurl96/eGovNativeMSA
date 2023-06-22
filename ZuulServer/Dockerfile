FROM eclipse-temurin:8
EXPOSE 8080
ADD ./target/ZuulServer-0.0.1-SNAPSHOT.jar zuul.jar
ENTRYPOINT ["java", "-jar", "zuul.jar"]