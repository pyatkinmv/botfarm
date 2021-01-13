FROM adoptopenjdk/openjdk11:alpine-jre
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
ENV SPRING_PROFILES_ACTIVE=prod
COPY target/*-spring-boot.jar /usr/app/botfarm.jar
ENTRYPOINT ["java", "-jar", "/usr/app/botfarm.jar"]
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
