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
