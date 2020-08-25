# Hypertrace GraphQL
Hypertrace GraphQL service serves the GraphQL API which will be used by Hypertrace UI.

## Testing

`./gradlew test`

## Running

`./gradlew run`

## Ports

GraphQL Service runs on port 23431 at `/graphql` by default


## Queries
Here are some of the important graphql queries:

### 1. Get all traces in provided time range

```graphql
curl -s http://localhost:2020/graphql -H 'Content-Type: application/graphql' -d\
'{
  traces(
    type: API_TRACE
    between: {
      startTime: "2015-01-01T00:00:00.000Z"
      endTime: "2025-01-01T00:00:00.000Z"
    }
  ) {
    results {
      id
    }
  }
}'
```

### 2. Find trace using TraceID

```graphql
curl -s http://localhost:2020/graphql -H 'Content-Type: application/graphql' -d \
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
    results {
      id
      apiName: attribute(key: "apiName") 
    }
  }
}'
```

### 3. Get all services your application is using

```graphql
curl -s http://localhost:2020/graphql  -H 'Content-Type: application/graphql' -d \
'{
  entities(
    type: SERVICE 
    between: {
      startTime: "2015-01-01T00:00:00Z"
      endTime: "2025-01-01T00:00:00Z"
    }
    offset: 0
  ) {
    results {
      id
      name: attribute(key: "name")
    }
    total
  }
}'
```

### 4. Get all backends your application is using

```graphql
curl -s http://localhost:2020/graphql -H 'Content-Type: application/graphql' -d \
'{
  entities(
    type: BACKEND
    between: {
      startTime: "2015-01-01T00:00:00Z"
      endTime: "2025-01-01T00:00:00Z"
    }
    offset: 0
  ) {
    results {
      id
      name: attribute(key: "name")
    }
    total
  }
}'
```

### 5. Get all API's your application is using

```graphql
curl -s http://localhost:2020/graphql -H 'Content-Type: application/graphql' -d \
'{
  entities(
    type: API
    between: {
      startTime: "2015-01-01T00:00:00Z"
      endTime: "2025-01-01T00:00:00Z"
    }
    offset: 0
  ) {
    results {
      id
      name: attribute(key: "name")
    }
    total
  }
}'
```

### 6. Get service and backend dependency graph

```graphql
curl -s http://localhost:2020/graphql -H 'Content-Type: application/graphql' -d \
'{
  entities(
    type: SERVICE
    limit: 100
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
            id
            name: attribute(key: "name")
          }
        }
      }
      outgoingEdges_BACKEND: outgoingEdges(neighborType: BACKEND) {
        results {
          neighbor {
            id
            name: attribute(key: "name")
          }
        }
      }
      incomingEdges_SERVICE: incomingEdges(neighborType: SERVICE) {
        results {
          neighbor {
            id
            name: attribute(key: "name")
          }
        }
      }
    }
  }
}'
```