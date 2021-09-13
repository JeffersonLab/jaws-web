# jaws-web-admin
Web Admin interface for [JAWS](https://github.com/JeffersonLab/jaws).

## Quick Start with Compose
1. Grab project
```
git clone https://github.com/JeffersonLab/jaws-web-admin
cd jaws-web-admin
```
2. Launch Docker
```
docker-compose up
```
3. Launch web browser
```
http://localhost:8080/jaws-web-admin
```
**Note**: The docker-compose services require significant system resources - tested with 4 CPUs and 4GB memory.

**Note**: The docker-compose up command implicitly reads both _docker-compose.yml_ and _docker-compose.override.yml_.

## Configure
The following environment variables are required:

| Name | Description |
|----------|---------|
| BOOTSTRAP_SERVER | Host and port pair pointing to a Kafka server to bootstrap the client connection to a Kafka Cluser; example: `kafka:9092` |
| SCHEMA_REGISTRY | URL to Confluent Schema Registry; example: `http://registry:8081` |

## Docker
```
docker pull slominskir/jaws-web-admin
```
Image hosted on [DockerHub](https://hub.docker.com/r/slominskir/jaws-web-admin)

**Note**: When developing the app you can mount the build artifact into the container by substituting the docker-compose up command with:
```
docker-compose -f docker-compose.yml -f docker-compose-dev.yml up
```
