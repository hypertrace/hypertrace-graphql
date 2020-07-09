plugins {
  `java-library`
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
}

dependencies {
  api("com.google.inject:guice:4.2.3")
  api("com.graphql-java:graphql-java:14.0")
  api("org.hypertrace.core.graphql:hypertrace-core-graphql-attribute-store")
  api("org.hypertrace.core.graphql:hypertrace-core-graphql-context")
  api("io.reactivex.rxjava3:rxjava:3.0.2")
  api("io.github.graphql-java:graphql-java-annotations:8.0")

  annotationProcessor("org.projectlombok:lombok:1.18.12")
  compileOnly("org.projectlombok:lombok:1.18.12")

  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-deserialization")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-schema-utils")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-common-schema")
}

