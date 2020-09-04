plugins {
  `java-library`
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
}

dependencies {
  api("com.google.inject:guice:4.2.3")
  api("com.graphql-java:graphql-java:14.0")
  api("org.hypertrace.core.graphql:hypertrace-core-graphql-spi")
  api("io.github.graphql-java:graphql-java-annotations:8.0")
  api(project(":hypertrace-graphql-metric-schema"))
  api("org.hypertrace.core.graphql:hypertrace-core-graphql-common-schema")

  annotationProcessor("org.projectlombok:lombok:1.18.12")
  compileOnly("org.projectlombok:lombok:1.18.12")

  implementation("org.slf4j:slf4j-api:1.7.3")
  implementation("io.reactivex.rxjava3:rxjava:3.0.2")
  implementation("org.hypertrace.gateway.service:gateway-service-api:0.1.0")
  implementation("com.google.protobuf:protobuf-java-util:3.11.4")
  implementation("com.google.guava:guava:29.0-jre")

  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-context")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-grpc-utils")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-schema-utils")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-attribute-store")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-deserialization")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-rx-utils")

}
