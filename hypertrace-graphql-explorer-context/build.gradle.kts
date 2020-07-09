plugins {
  `java-library`
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
}

dependencies {
  api("com.google.inject:guice:4.2.3")
  api("io.reactivex.rxjava3:rxjava:3.0.2")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-attribute-store")
  implementation("org.hypertrace.core.graphql:hypertrace-core-graphql-common-schema")
  implementation(project(":hypertrace-graphql-explorer-schema"))
}