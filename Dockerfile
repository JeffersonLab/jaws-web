ARG BUILD_IMAGE=gradle:7.4-jdk17
ARG RUN_IMAGE=quay.io/wildfly/wildfly:26.0.1.Final
ARG RUN_USER=jboss

FROM ${BUILD_IMAGE} as builder
USER root
WORKDIR /
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
