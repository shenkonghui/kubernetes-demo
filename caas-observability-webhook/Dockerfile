FROM openjdk:11.0.5-slim
COPY target/caas-admission-webhook-0.0.1-SNAPSHOT.jar caas-admission-webhook-0.0.1-SNAPSHOT.jar
CMD java -jar caas-admission-webhook-0.0.1-SNAPSHOT.jar --spring.config.location=/usr/local/conf/application.yml