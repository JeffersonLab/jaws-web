#!/bin/bash

echo "-------------------------------------------------------"
echo "Step 1: Waiting for Schema Registry to start listening "
echo "-------------------------------------------------------"
while [ $(curl -s -o /dev/null -w %{http_code} $SCHEMA_REGISTRY/schemas/types) -eq 000 ] ; do
  echo -e $(date) " Registry listener HTTP state: " $(curl -s -o /dev/null -w %{http_code} $SCHEMA_REGISTRY/schemas/types) " (waiting for 200)"
  sleep 5
done

# Launch original container CMD
/opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 &

sleep infinity
