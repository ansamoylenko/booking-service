FROM maven:3.9.7
WORKDIR /app
ADD . /app
RUN mvn package -Dmaven.test.skip=true
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005","-jar", "target/booking-service-0.0.1-SNAPSHOT.jar"]
