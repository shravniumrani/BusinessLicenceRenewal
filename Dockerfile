FROM tomcat:10.1.60-jdk21-temurin

ENV LICENCE_DATA_DIR=/var/lib/licence
RUN mkdir -p /var/lib/licence

COPY target/business-licence-renewal.war /usr/local/tomcat/webapps/business-licence-renewal.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
