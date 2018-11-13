FROM openjdk:8-jdk-alpine as build
WORKDIR /workspace

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
#run here to avoid re-downloading dependencies if pom hasn't changed
RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw install -DskipTests

FROM openjdk:8-jdk-alpine
COPY --from=build /workspace/target/ipv4-geo-lookup.jar .
ENTRYPOINT ["java","-jar","ipv4-geo-lookup.jar"]