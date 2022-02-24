# REQUEST BIN SERVICES

[![CI](https://github.com/forr-bsingh/request-bin-services/actions/workflows/image.yml/badge.svg?branch=main)](https://github.com/forr-bsingh/request-bin-services/actions/workflows/image.yml)

## **Possible operations:**

    1. Create a request bin: POST /bins
    2. Delete a request bin: DELETE /bins/{name}
    3. Get a request bin: GET /bins/{name}
    4. Get all audit ops for request bin: GET /bins/{name}/ops
    5. Post to a request bin: POST /bins/{name}/requests
    6. Get from a request bin: GET /bins/{name}/requests/{identifier}
    7. List from a request bin: GET /bins/{name}/requests
    8. Delete from a request bin: DELETE /bins/{name}/requests/{identifier}
    9. Put to a request bin: PUT /bins/{name}/requests/{identifier}
    10. Patch to a request bin: PATCH /bins/{name}/requests/{identifier}
    11. List all schema attributes: GET /schema/attributes
    12. Resolve schema: POST /schema/resolve


## **Env configuration for local setup:**

    1. app.home - set it to . (pwd)
    2. profile - development
    3. port - 9090
    4. redis.host - localhost
    5. redis.port - 6379
    6. redis.mode - standalone or cluster
    7. redis.nodes - localhost:6378,localhost:6379,localhost:6380

## **Docker Setup:**

Command list :
- Kill all running containers : docker stop $(docker ps -q)
- Exec using "docker run" : docker run  -e {env propert(y)ies} -p {inbound-port}:{outbound-port} {image-name}
- Alias for dm-disk : alias dm-disk='docker run --rm -it -v /:/docker alpine:edge $@'
- List content of volume : dm-disk ls -l /docker/<path of docker volume mount point>

Build Command for service image :

 - docker build -t bsingh1904/request-bin-services:latest .

 - docker build --build-arg SERVICE_NAME=request-bin --build-arg SERVICE_PORT=9090 --build-arg GRPC_PORT=9089 -t bsingh1904/request-bin-services:latest .

 - docker buildx build --push --platform linux/arm64/v8,linux/amd64  --tag bsingh1904/request-bin-services:latest .
