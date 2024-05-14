# jaws-admin-gui [![Java CI with Gradle](https://github.com/JeffersonLab/jaws-admin-gui/actions/workflows/ci.yml/badge.svg)](https://github.com/JeffersonLab/jaws-admin-gui/actions/workflows/ci.yml) [![Docker](https://img.shields.io/docker/v/jeffersonlab/jaws-admin-gui?sort=semver&label=DockerHub)](https://hub.docker.com/r/jeffersonlab/jaws-admin-gui)
Web Admin interface for [JAWS](https://github.com/JeffersonLab/jaws) to manage alarm registrations and view notifications.

<p>
<a href="#"><img src="https://github.com/JeffersonLab/jaws-admin-gui/raw/main/src/main/webapp/resources/img/screenshot1.png"/></a>     
</p>

---
 - [Overview](https://github.com/JeffersonLab/jaws-admin-gui#overview)
 - [Quick Start with Compose](https://github.com/JeffersonLab/jaws-admin-gui#quick-start-with-compose) 
 - [Install](https://github.com/JeffersonLab/jaws-admin-gui#install)
 - [Configure](https://github.com/JeffersonLab/jaws-admin-gui#configure)
 - [Build](https://github.com/JeffersonLab/jaws-admin-gui#build)
 - [Develop](https://github.com/JeffersonLab/jaws-admin-gui#develop)
 - [Release](https://github.com/JeffersonLab/jaws-admin-gui#release)
 - [Deploy](https://github.com/JeffersonLab/jaws-admin-gui#deploy) 
 - [See Also](https://github.com/JeffersonLab/jaws-admin-gui#see-also)
---

## Overview
Alarm system registration data consists of locations, categories, classes, and instances.  Collectively the data forms effective registrations.

## Quick Start with Compose
1. Grab project
```
git clone https://github.com/JeffersonLab/jaws-admin-gui
cd jaws-admin-gui
```
2. Launch [Compose](https://github.com/docker/compose)
```
docker compose up
```
3. Launch web browser
```
http://localhost:8080/jaws
```
**Note**: Login with demo username "tbrown" and password "password".

**Note**: The docker-compose services require significant system resources - tested with 4 CPUs and 4GB memory.

**See**: [Docker Compose Strategy](https://gist.github.com/slominskir/a7da801e8259f5974c978f9c3091d52c)

## Install
This application requires a Java 11+ JVM and standard library to run, plus a Java EE 8+ application server (developed with Wildfly).

   1. Install service [dependencies](https://github.com/JeffersonLab/jaws-admin-gui/blob/main/deps.yaml)
   1. Download [Wildfly 26.1.3](https://www.wildfly.org/downloads/)
   1. [Configure](https://github.com/JeffersonLab/jaws-admin-gui#configure) Wildfly and start it
   1. Download [jaws.war](https://github.com/JeffersonLab/jaws-admin-gui/releases) and deploy it to Wildfly
   1. Navigate your web browser to localhost:8080/jaws


## Configure

### Configtime
Wildfly must be pre-configured before the first deployment of the app. The [wildfly bash scripts](https://github.com/JeffersonLab/wildfly#configure) can be used to accomplish this. See the [Dockerfile](https://github.com/JeffersonLab/jaws-admin-gui/blob/main/Dockerfile) for an example.

### Runtime
The following environment variables are required:

| Name | Description |
|----------|---------|
| BOOTSTRAP_SERVERS | Host and port pair pointing to a Kafka server to bootstrap the client connection to a Kafka Cluser; example: `kafka:9092` |
| SCHEMA_REGISTRY | URL to Confluent Schema Registry; example: `http://registry:8081` |

## Build
This project is built with [Java 17](https://adoptium.net/) (compiled to Java 11 bytecode), and uses the [Gradle 7](https://gradle.org/) build tool to automatically download dependencies and build the project from source:

```
git clone https://github.com/JeffersonLab/jaws-admin-gui
cd jaws-admin-gui
gradlew build
```
**Note**: If you do not already have Gradle installed, it will be installed automatically by the wrapper script included in the source

**Note for JLab On-Site Users**: Jefferson Lab has an intercepting [proxy](https://gist.github.com/slominskir/92c25a033db93a90184a5994e71d0b78)

**See**: [Docker Development Quick Reference](https://gist.github.com/slominskir/a7da801e8259f5974c978f9c3091d52c#development-quick-reference)

## Develop
In order to iterate rapidly when making changes it's often useful to run the app directly on the local workstation, perhaps leveraging an IDE.  In this scenario run the service dependencies with:
```
docker compose -f deps.yaml up
# OR if on JLab network use control system config with `jlab-deps.yaml` instead.
```
**Note**: The local install of Wildfly should be [configured](https://github.com/JeffersonLab/jaws-admin-gui#configure) to proxy connections to services via localhost and therefore the environment variables should contain:
```
KEYCLOAK_BACKEND_SERVER_URL=http://localhost:8081
FRONTEND_SERVER_URL=https://localhost:8443
```
Further, the local DataSource must also leverage localhost port forwarding so the `standalone.xml` connection-url field should be: `jdbc:oracle:thin:@//localhost:1521/xepdb1`.  

The [server](https://github.com/JeffersonLab/wildfly/blob/main/scripts/server-setup.sh) and [app](https://github.com/JeffersonLab/wildfly/blob/main/scripts/app-setup.sh) setup scripts can be used to setup a local instance of Wildfly. 

## Release
1. Bump the version number in the VERSION file and commit and push to GitHub (using [Semantic Versioning](https://semver.org/)).
2. The [CD](https://github.com/JeffersonLab/jaws-admin-gui/blob/main/.github/workflows/cd.yml) GitHub Action should run automatically invoking:
    - The [Create release](https://github.com/JeffersonLab/java-workflows/blob/main/.github/workflows/gh-release.yml) GitHub Action to tag the source and create release notes summarizing any pull requests.   Edit the release notes to add any missing details.  A war file artifact is attached to the release.
    - The [Publish docker image](https://github.com/JeffersonLab/container-workflows/blob/main/.github/workflows/docker-publish.yml) GitHub Action to create a new demo Docker image, and bump the [compose.override.yaml](https://github.com/JeffersonLab/jaws-admin-gui/blob/main/compose.override.yaml) to use the new image.

## Deploy
At JLab this app is found at [ace.jlab.org/jaws](https://ace.jlab.org/jaws) and internally at [acctest.acc.jlab.org/jaws](https://acctest.acc.jlab.org/jaws).  However, those servers are proxies for `jaws.acc.jlab.org` and `jawstest.acc.jlab.org` respectively.  This app makes up one service in a set of services defined in a compose file that make up the JAWS system and deployments are managed by [JAWS](https://github.com/JeffersonLab/jaws).

## See Also
- [JLab alarm data](https://github.com/JeffersonLab/alarms)
- [Developer Notes](https://github.com/JeffersonLab/jaws-admin-gui/wiki/Developer-Notes)
