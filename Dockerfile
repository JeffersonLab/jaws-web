FROM jboss/wildfly:25.0.0.Final
ADD ./dockerWarVolume/jaws-admin-gui.war /opt/jboss/wildfly/standalone/deployments/
ENTRYPOINT ["/docker-entrypoint.sh"]
