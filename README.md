# Hypertrace GraphQL
Hypertrace GraphQL service serves the GraphQL API which will be used by Hypertrace UI.

## Description

| ![space-1.jpg](https://hypertrace-docs.s3.amazonaws.com/arch/ht-query.png) | 
|:--:| 
| *Hypertrace Query Architecture* |

[Hypertrace-UI](https://github.com/hypertrace/hypertrace-ui) talks to hypertrace-GraphQL service which serves the GraphQL API which queries data from downstream services. GraphQL services talks to different grpc services to form the response including [attribute-service](https://github.com/hypertrace/attribute-service), [entity-service](https://github.com/hypertrace/entity-service), [gateway-service](https://github.com/hypertrace/gateway-service) and [config-service](https://github.com/hypertrace/config-service). 


### Queries
Here are some of the important GraphQL queries:


#### 1. Verify trace exists

```graphql
curl -s localhost:2020/graphql -H 'Content-Type: application/graphql' -d \
'{
  traces(
    type: API_TRACE
    between: {
      startTime: "2015-01-01T00:00:00.000Z"
      endTime: "2025-01-01T00:00:00.000Z"
    }
    filterBy: [
      {
        operator: EQUALS
        value: "348bae39282251a5"
        type: ID
        idType: API_TRACE
      }
    ]
  ) {
    total
  }
}'
```


#### 2. Get all service names

```graphql
curl -s localhost:2020/graphql  -H 'Content-Type: application/graphql' -d \
'{
  entities(
    type: SERVICE 
    between: {
      startTime: "2015-01-01T00:00:00Z"
      endTime: "2025-01-01T00:00:00Z"
    }
  ) {
    results {
      name: attribute(key: "name")
    }
    total
  }
}'
```

#### 3. Get all backend names

```graphql
curl -s localhost:2020/graphql -H 'Content-Type: application/graphql' -d \
'{
  entities(
    type: BACKEND
    between: {
      startTime: "2015-01-01T00:00:00Z"
      endTime: "2025-01-01T00:00:00Z"
    }
  ) {
    results {
      name: attribute(key: "name")
    }
    total
  }
}'
```

#### 4. Get all API names

```graphql
curl -s localhost:2020/graphql -H 'Content-Type: application/graphql' -d \
'{
  entities(
    type: API
    between: {
      startTime: "2015-01-01T00:00:00Z"
      endTime: "2025-01-01T00:00:00Z"
    }
  ) {
    results {
      name: attribute(key: "name")
    }
    total
  }
}'
```

#### 5. Get service and backend dependency graph

```graphql
curl -s localhost:2020/graphql -H 'Content-Type: application/graphql' -d \
'{
  entities(
    type: SERVICE
    between: {
      startTime: "2015-01-01T00:00:00.000Z"
      endTime: "2025-01-01T00:00:00.000Z"
    }
  ) {
    results {
      id
      name: attribute(key: "name")
      outgoingEdges_SERVICE: outgoingEdges(neighborType: SERVICE) {
        results {
          neighbor {
            name: attribute(key: "name")
          }
        }
      }
      outgoingEdges_BACKEND: outgoingEdges(neighborType: BACKEND) {
        results {
          neighbor {
            name: attribute(key: "name")
          }
        }
      }
      incomingEdges_SERVICE: incomingEdges(neighborType: SERVICE) {
        results {
          neighbor {
            name: attribute(key: "name")
          }
        }
      }
    }
  }
}'
```


## Building locally
The Hypertrace GraphQl service uses gradle to compile/install/distribute. Gradle wrapper is already part of the source code. To build Hypertrace GraphQL image, run:

```
./gradlew dockerBuildImages
```

## Testing

### Running unit tests
Run `./gradlew test` to execute unit tests. 

### Testing image

To test your image using the docker-compose setup follow the steps:

- Commit you changes to a branch say `graphql-service-test`.
- Go to [hypertrace-service](https://github.com/hypertrace/hypertrace-service) and checkout the above branch in the submodule.
```
cd hypertrace-graphql && git checkout graphql-service-test && cd ..
```
- Change tag for `hypertrace-service` from `:main` to `:test` in [docker-compose file](https://github.com/hypertrace/hypertrace/blob/main/docker/docker-compose.yml) like this.

```yaml
  hypertrace-service:
    image: hypertrace/hypertrace-service:test
    container_name: hypertrace-service
    ...
```
- and then run `docker-compose up` to test the setup.

### Helm setup
Add image repository and tag in values.yaml file [here](https://github.com/hypertrace/hypertrace/blob/main/kubernetes/platform-services/values.yaml) like below and then run `./hypertrace.sh install` again and you can test your image!

```yaml
hypertrace-graphql-service:
  image:
    repository: "hypertrace/hypertrace-graphql-service"
    tagOverride: "test"
 ```

## Docker Image Source:
- [DockerHub > Hypertrace GraphQL](https://hub.docker.com/r/hypertrace/hypertrace-graphql-service/)
