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
  api("org.hypertrace.core.graphql:hypertrace-core-graphql-common-schema")
  api(project(":hypertrace-graphql-labels-schema-api"))

  annotationProcessor("org.projectlombok:lombok")
  compileOnly("org.projectlombok:lombok")

  implementation("org.slf4j:slf4j-api")
  implementation("io.reactivex.rxjava3:rxjava")
  implementation("org.hypertrace.config.service:labels-config-service-api")
  implementation("org.hypertrace.config.service:label-application-rule-config-service-api")
  implementation("com.google.protobuf:protobuf-java-util")
  implementation("com.google.guava:guava")

  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-context")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-grpc-utils")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-schema-utils")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-rx-utils")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-deserialization")

  implementation(project(":hypertrace-graphql-entity-schema"))

  implementation(project(":hypertrace-graphql-service-config"))
}

tasks.test {
  useJUnitPlatform()
}
