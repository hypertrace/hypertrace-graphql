plugins {
  `java-library`
  jacoco
  id("org.hypertrace.jacoco-report-plugin")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
}

dependencies {
  api("org.hypertrace.core.graphql:hypertrace-core-graphql-spi")
  api("com.graphql-java-kickstart:graphql-java-servlet:9.1.0")

  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-schema-registry")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-context")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-deserialization")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-grpc-utils")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-schema-utils")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-gateway-service-utils")
  implementation(project(":hypertrace-graphql-attribute-scope"))
  implementation(project(":hypertrace-graphql-gateway-service-metric-utils"))
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-attribute-store")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-common-schema")
  implementation(project(":hypertrace-graphql-metric-schema"))
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-metadata-schema")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-span-schema")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-trace-schema")
  implementation(project(":hypertrace-graphql-entity-schema"))
  implementation(project(":hypertrace-graphql-explorer-schema"))
  implementation(project(":hypertrace-graphql-explorer-context"))
  implementation(project(":hypertrace-graphql-entity-type"))

  implementation("org.slf4j:slf4j-api:1.7.3")
  implementation("com.google.inject:guice:4.2.3")
  implementation("io.grpc:grpc-netty:1.30.2")

  testImplementation("org.junit.jupiter:junit-jupiter:5.5.1")
  testImplementation("org.mockito:mockito-core:3.2.4")
  testImplementation("org.mockito:mockito-junit-jupiter:3.2.4")
}

tasks.test {
  useJUnitPlatform()
}
