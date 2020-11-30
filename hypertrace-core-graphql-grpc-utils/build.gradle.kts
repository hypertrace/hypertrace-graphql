plugins {
  `java-library`
  jacoco
  id("org.hypertrace.jacoco-report-plugin")
}

dependencies {
  api("com.google.inject:guice")
  api("com.graphql-java:graphql-java")
  api("io.grpc:grpc-api")
  api("io.grpc:grpc-core")
  api("io.grpc:grpc-stub")
  api(project(":hypertrace-core-graphql-context"))

  implementation("org.hypertrace.core.grpcutils:grpc-context-utils")
  implementation("org.hypertrace.core.grpcutils:grpc-client-utils")
  implementation("io.grpc:grpc-context")
  implementation("io.reactivex.rxjava3:rxjava")
  implementation("org.slf4j:slf4j-api")
  implementation(project(":hypertrace-core-graphql-spi"))

  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation("org.mockito:mockito-core")
  testImplementation("org.mockito:mockito-junit-jupiter")

  testRuntimeOnly("io.grpc:grpc-netty")
}

tasks.test {
  useJUnitPlatform()
}
