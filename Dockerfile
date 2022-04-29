# build
FROM gradle:5.3.0-jdk AS TEMP_BUILD_IMAGE
USER root
WORKDIR /app
COPY . .
RUN ./gradlew clean build

# production
FROM alpine
WORKDIR /app
RUN apk update \
 && apk add lighttpd \
 && rm -rf /var/cache/apk/*
COPY --from=TEMP_BUILD_IMAGE /app/build/distributions /var/www/localhost/htdocs
EXPOSE 80
CMD ["lighttpd", "-D", "-f", "/etc/lighttpd/lighttpd.conf"]
