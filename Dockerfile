FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/ads-mockup-0.0.1-SNAPSHOT-standalone.jar /ads-mockup/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/ads-mockup/app.jar"]
