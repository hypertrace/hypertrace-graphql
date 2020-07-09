plugins {
  `java-library`
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
}

dependencies {
  api("com.google.inject:guice:4.2.3")
  api("org.hypertrace.gateway.service:gateway-service-api:0.1.0")
  api("org.hypertrace.core.graphql:hypertrace-core-graphql-attribute-store")
  api("io.reactivex.rxjava3:rxjava:3.0.2")
  api("org.hypertrace.core.graphql:hypertrace-core-graphql-common-schema")
  api(project(":hypertrace-graphql-metric-schema"))
  api("org.hypertrace.core.graphql:hypertrace-core-graphql-gateway-service-utils")

  annotationProcessor("org.projectlombok:lombok:1.18.12")
  compileOnly("org.projectlombok:lombok:1.18.12")
}
