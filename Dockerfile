ARG BUILD_IMAGE=gradle:7.4-jdk17-alpine
ARG RUN_IMAGE=jeffersonlab/wildfly:1.2.0
ARG CUSTOM_CRT_URL=http://pki.jlab.org/JLabCA.crt

################## Stage 0
FROM ${BUILD_IMAGE} as builder
ARG CUSTOM_CRT_URL
USER root
WORKDIR /
RUN if [ -z "${CUSTOM_CRT_URL}" ] ; then echo "No custom cert needed"; else \
       wget -O /usr/local/share/ca-certificates/customcert.crt $CUSTOM_CRT_URL \
       && update-ca-certificates \
       && keytool -import -alias custom -file /usr/local/share/ca-certificates/customcert.crt -cacerts -storepass changeit -noprompt \
       && export OPTIONAL_CERT_ARG=--cert=/etc/ssl/certs/ca-certificates.crt \
    ; fi
COPY . /app
RUN cd /app  \
    && gradle build -x test --no-watch-fs $OPTIONAL_CERT_ARG \
    && cd build \
    &&  apk add openssl \
    && openssl genrsa -out localhost.key 2048 \
    && openssl req -key localhost.key -new -out localhost.csr -subj "/C=US/ST=Virginia/O=localhost dev/OU=IT Department/CN=localhost" \
    && openssl x509 -signkey localhost.key -in localhost.csr -req -days 99999 -out localhost.crt \
    && openssl pkcs12 -export -in localhost.crt -inkey localhost.key -name localhost -password pass:changeit > localhost.p12 \
    && keytool -importkeystore -srckeystore localhost.p12 -destkeystore server.p12 -srcstoretype pkcs12 -alias localhost -deststorepass changeit -srcstorepass changeit

################## Stage 1
FROM ${RUN_IMAGE} as runner
COPY --from=builder /app/docker/app/*.env /
COPY --from=builder /app/docker-entrypoint.sh /docker-entrypoint.sh
COPY --from=builder /app/build/server.p12 /opt/jboss/wildfly/standalone/configuration
USER root
RUN /server-setup.sh /server-setup.env wildfly_start_and_wait \
     && /app-setup.sh /app-setup.env config_keycloak_client \
     && /server-setup.sh /server-setup.env config_provided \
     && /server-setup.sh /server-setup.env config_ssl \
     && /server-setup.sh /server-setup.env wildfly_reload \
     && /server-setup.sh /server-setup.env wildfly_stop \
     && rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history
USER jboss
COPY --from=builder /app/build/libs /opt/jboss/wildfly/standalone/deployments
ENTRYPOINT ["/docker-entrypoint.sh"]
