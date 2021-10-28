FROM jboss/wildfly
ADD ./dockerWarVolume/jaws-admin-gui.war /opt/jboss/wildfly/standalone/deployments/
