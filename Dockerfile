# Pull base image 
FROM adoptopenjdk/openjdk11:latest

# Maintainer 
MAINTAINER "kishorngr@gmail.com" 

# Copy to images tomcat path 
COPY target/SpringS3Amazon-0.0.1.jar /demo.jar

EXPOSE 9000
CMD ["java", "-jar", "/demo.jar"]
