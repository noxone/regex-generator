# build
FROM gradle:7.4.2-jdk11 AS TEMP_BUILD_IMAGE
WORKDIR /app
COPY . .

# Install Firefox
RUN apt-get update && apt-get install -y \
	firefox \
	--no-install-recommends
RUN rm -rf /var/lib/apt/lists/*

# Build the web application
RUN ./gradlew clean build
RUN rm /app/build/distributions/regex-generator.js.map

# production
FROM alpine
WORKDIR /app
RUN apk update \
 && apk add lighttpd \
 && rm -rf /var/cache/apk/*
COPY --from=TEMP_BUILD_IMAGE /app/build/distributions /var/www/localhost/htdocs
EXPOSE 80
CMD ["lighttpd", "-D", "-f", "/etc/lighttpd/lighttpd.conf"]
