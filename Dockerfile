FROM jboss/wildfly
ADD ./dockerWarVolume/jaws-web-admin.war /opt/jboss/wildfly/standalone/deployments/
