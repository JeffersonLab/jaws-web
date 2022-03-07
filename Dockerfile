ARG BUILD_IMAGE=gradle:7.4-jdk17
ARG RUN_IMAGE=quay.io/wildfly/wildfly:26.0.1.Final
ARG RUN_USER=jboss
ARG CUSTOM_CRT_URL

FROM ${BUILD_IMAGE} as builder
USER root
WORKDIR /
RUN if [ -z "$CUSTOM_CRT_URL" ] ; then echo "No custom cert needed"; else \
       wget -O /usr/local/share/ca-certificates/customcert.crt $CUSTOM_CRT_URL \
       && update-ca-certificates \
       && export OPTIONAL_CERT_ARG=--cert=/etc/ssl/certs/ca-certificates.crt \
    ; fi
COPY . /app
RUN cd /app && gradle build -x test --no-watch-fs

FROM ${RUN_IMAGE}
USER root
COPY --from=builder /app/docker-entrypoint.sh /docker-entrypoint.sh
COPY --from=builder /app/build/libs /opt/jboss/wildfly/standalone/deployments
RUN chown -R ${RUN_USER}:0 ${JBOSS_HOME} \
    && chmod -R g+rw ${JBOSS_HOME}
USER ${RUN_USER}
ENTRYPOINT ["/docker-entrypoint.sh"]
