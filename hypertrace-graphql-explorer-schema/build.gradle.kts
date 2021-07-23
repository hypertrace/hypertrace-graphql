plugins {
  `java-library`
  jacoco
  id("org.hypertrace.jacoco-report-plugin")
}

dependencies {
  api("com.google.inject:guice")
  api("com.graphql-java:graphql-java")
  api("org.hypertrace.core.graphql:hypertrace-core-graphql-spi")
  api("io.github.graphql-java:graphql-java-annotations")
  api(project(":hypertrace-graphql-metric-schema"))

  annotationProcessor("org.projectlombok:lombok")
  compileOnly("org.projectlombok:lombok")

  implementation("org.slf4j:slf4j-api")
  implementation("io.reactivex.rxjava3:rxjava")
  implementation("org.hypertrace.gateway.service:gateway-service-api")
  implementation("com.google.protobuf:protobuf-java-util")

  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-context")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-grpc-utils")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-common-schema")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-attribute-store")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-deserialization")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-schema-utils")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-rx-utils")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-request-transformation")

  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation("org.mockito:mockito-core")
  testImplementation("org.mockito:mockito-junit-jupiter")
}

tasks.test {
  useJUnitPlatform()
}
