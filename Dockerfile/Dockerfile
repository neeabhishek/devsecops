#Main Runtime Stage
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
COPY spring-boot/target/enterprise-devsecops-demo-1.0.0.jar /app/enterprise-devsecops-demo-1.0.0.jar
RUN groupadd -r appadmin && \
    useradd -r -g appadmin appadmin && \
    chown -R appadmin:appadmin /app && \
    chmod -R 750 /app

USER appadmin
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-jar","enterprise-devsecops-demo-1.0.0.jar"]

#Main Debug Stage
FROM eclipse-temurin:21-jre AS debug
WORKDIR /app
COPY --from=runtime /app/enterprise-devsecops-demo-1.0.0.jar /app/enterprise-devsecops-demo-1.0.0.jar

USER root
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-jar","enterprise-devsecops-demo-1.0.0.jar"]
