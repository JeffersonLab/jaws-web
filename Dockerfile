FROM jboss/wildfly:25.0.0.Final
ADD ./dockerWarVolume/jaws-admin-gui.war /opt/jboss/wildfly/standalone/deployments/
ADD ./docker-entrypoint.sh /
ENTRYPOINT ["/docker-entrypoint.sh"]
