ARG BUILD_IMAGE=gradle:7.3.3-jdk17

# BUILD_TYPE should be one of 'remote-src', 'local-src', 'local-artifact'
ARG BUILD_TYPE=remote-src

###
# Remote source scenario
###
FROM ${BUILD_IMAGE} as remote-src

USER root
WORKDIR /

RUN git clone https://github.com/JeffersonLab/jaws-admin-gui \
   && cd jaws-admin-gui \
   && gradle build -x test --no-watch-fs

###
# Local source scenario
#
# This scenario is the only one that needs .dockerignore
###
FROM ${BUILD_IMAGE} as local-src

USER root
WORKDIR /

RUN mkdir /jaws-admin-gui

COPY . /jaws-admin-gui

RUN cd /jaws-admin-gui && gradle build -x test --no-watch-fs

###
# Local Artifact scenario
#
# If we used local-src here we'd trigger Docker cache changes before this stage/layer is reached
# and the whole point of local-artifact is to narrowly target an artifact and leverage caching
###
FROM remote-src as local-artifact

USER root
WORKDIR /

# Single out deployment artifact to leverage Docker build caching
COPY ./jaws-admin-gui/build/libs/jaws-admin-gui.war /jaws-admin-gui/build/libs/

###
# Build type chooser / resolver stage
#
# The "magic" is due to Docker honoring dynamic arguments for an image to run.
#
###
FROM ${BUILD_TYPE} as builder-chooser

###
# Final product stage brings it all together in as small and few layers as possible.
###
FROM quay.io/wildfly/wildfly:26.0.1.Final as final-product

USER root

# This must be last and separate from other copy command for caching purposes (local-artifact scenario)
COPY --from=builder-chooser /jaws-admin-gui/build/libs /opt/jboss/wildfly/standalone/deployments

RUN chown -R jboss:0 ${JBOSS_HOME} \
    && chmod -R g+rw ${JBOSS_HOME}

USER jboss

ENTRYPOINT ["/docker-entrypoint.sh"]
