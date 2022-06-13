ARG BUILD_IMAGE=gradle:7.4-jdk17-alpine
ARG RUN_IMAGE=quay.io/wildfly/wildfly:26.1.1.Final
ARG NODE_IMAGE=node:18.3.0-alpine3.16

################## Stage 0
FROM ${NODE_IMAGE} AS node

################## Stage 1
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
COPY --from=node /usr/lib /usr/lib
COPY --from=node /usr/local/share /usr/local/share
COPY --from=node /usr/local/lib /usr/local/lib
COPY --from=node /usr/local/include /usr/local/include
COPY --from=node /usr/local/bin /usr/local/bin
RUN node --version
COPY . /app
RUN cd /app && gradle build -x test --no-watch-fs $OPTIONAL_CERT_ARG

################## Stage 2
FROM ${RUN_IMAGE} as runner
ARG RUN_USER=jboss
USER root
COPY --from=builder /app/docker-entrypoint.sh /docker-entrypoint.sh
COPY --from=builder /app/build/libs /opt/jboss/wildfly/standalone/deployments
RUN chown -R ${RUN_USER}:0 ${JBOSS_HOME} \
    && chmod -R g+rw ${JBOSS_HOME}
USER ${RUN_USER}
ENTRYPOINT ["/docker-entrypoint.sh"]
