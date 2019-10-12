FROM gradle:jdk10 as builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle bootJar

FROM  adoptopenjdk/openjdk8-openj9:alpine-slim
COPY --from=builder /home/gradle/src/build/libs/NextTo-Blog-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom -Xms64M -Xss256K -XX:ParallelGCThreads=2 ","-jar","/app/app.jar"]

