plugins {
  `java-platform`
}

javaPlatform {
  allowDependencies()
}

dependencies {
  api(platform("org.hypertrace.core.graphql:hypertrace-core-graphql-platform"))
  constraints {
    api("org.hypertrace.entity.service:entity-type-service-rx-client:0.2.0")
  }
}