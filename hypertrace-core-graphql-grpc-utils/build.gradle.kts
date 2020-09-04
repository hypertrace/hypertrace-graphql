plugins {
  `java-library`
  jacoco
  id("org.hypertrace.jacoco-report-plugin")
}

dependencies {
  api("com.google.inject:guice:4.2.3")
  api("com.graphql-java:graphql-java:14.0")
  api("io.grpc:grpc-api:1.31.1")
  api("io.grpc:grpc-core:1.31.1")
  api("io.grpc:grpc-stub:1.31.1")
  api(project(":hypertrace-core-graphql-context"))

  implementation("org.hypertrace.core.grpcutils:grpc-context-utils:0.1.4")
  implementation("org.hypertrace.core.grpcutils:grpc-client-utils:0.1.4")
  implementation("io.grpc:grpc-context:1.31.1")
  implementation("io.reactivex.rxjava3:rxjava:3.0.2")
  implementation(project(":hypertrace-core-graphql-spi"))

  testImplementation("org.junit.jupiter:junit-jupiter:5.5.1")
  testImplementation("org.mockito:mockito-core:3.2.4")
  testImplementation("org.mockito:mockito-junit-jupiter:3.2.4")

  testRuntimeOnly("io.grpc:grpc-netty:1.31.1")
}

tasks.test {
  useJUnitPlatform()
}
