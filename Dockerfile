FROM openjdk:8-jre-alpine
VOLUME /tmp
ADD target/is-it-raining*.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]