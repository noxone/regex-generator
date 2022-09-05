# Defining environment APP_ENV is 'local' or 'github'
# -> local means local build
# -> github means github action build
ARG APP_ENV=local

#**************************************
# build stages used by local build only
#**************************************
FROM gradle:7.5.1-jdk11 AS TEMP_BUILD_IMAGE
WORKDIR /app
COPY . .

# Install Firefox
RUN echo 'Package: firefox' > /etc/apt/preferences.d/firefox-no-snap  && \
	echo 'Pin: origin "*.ubuntu.com"' >> /etc/apt/preferences.d/firefox-no-snap  && \ 
	echo 'Pin: origin "*.ubuntu.com"' >> /etc/apt/preferences.d/firefox-no-snap  && \
	echo 'Pin-Priority: -1' >> /etc/apt/preferences.d/firefox-no-snap && \
	apt-get update && apt purge firefox &&\
	apt-get install -y software-properties-common && \
	add-apt-repository ppa:mozillateam/ppa && \
	apt-get update && apt-get install -y \
	firefox \
	--no-install-recommends
RUN rm -rf /var/lib/apt/lists/*

# Build the web application
RUN ./gradlew clean build
RUN rm /app/build/distributions/regex-generator.js.map
#**************************************
# end of local build stages 
#**************************************

# local build only
FROM alpine:3.16 AS local-postinstall
WORKDIR /app
RUN apk update \
 && apk add lighttpd \
 && rm -rf /var/cache/apk/*
COPY --from=TEMP_BUILD_IMAGE /app/build/distributions /var/www/localhost/htdocs

# github action only
FROM alpine:3.16 AS github-postinstall
WORKDIR /app
RUN apk update \
 && apk add lighttpd \
 && rm -rf /var/cache/apk/*
COPY build/distributions /var/www/localhost/htdocs

# final stage (maybe empty)
FROM ${APP_ENV}-postinstall as final
EXPOSE 80
CMD ["lighttpd", "-D", "-f", "/etc/lighttpd/lighttpd.conf"]
