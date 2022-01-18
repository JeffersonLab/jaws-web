FROM quay.io/wildfly/wildfly:26.0.0.Final
ADD ./dockerWarVolume/jaws-admin-gui.war /opt/jboss/wildfly/standalone/deployments/
ADD ./docker-entrypoint.sh /
ENTRYPOINT ["/docker-entrypoint.sh"]
