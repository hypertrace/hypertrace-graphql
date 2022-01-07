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

  annotationProcessor("org.projectlombok:lombok")
  compileOnly("org.projectlombok:lombok")

  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-deserialization")
}

tasks.test {
  useJUnitPlatform()
}
