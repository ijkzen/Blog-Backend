FROM gradle:5.6.2-jdk8 as builder

COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle bootJar

FROM  adoptopenjdk/openjdk8-openj9
# set timezone
ARG TIME_ZONE=Asia/Shanghai
ENV TZ=${TIME_ZONE}

COPY --from=builder /home/gradle/src/build/libs/IJKZEN-BLOG-0.0.1-SNAPSHOT.jar /app/app.jar
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]

