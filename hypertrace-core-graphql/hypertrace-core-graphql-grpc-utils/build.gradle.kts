plugins {
  `java-library`
  jacoco
  alias(commonLibs.plugins.hypertrace.jacoco)
}

dependencies {
  api(commonLibs.guice)
  api(commonLibs.graphql.java)
  api(commonLibs.grpc.api)
  api(commonLibs.grpc.core)
  api(commonLibs.grpc.stub)
  api(projects.hypertraceCoreGraphqlContext)
  api(commonLibs.hypertrace.grpcutils.context)

  implementation(commonLibs.hypertrace.grpcutils.client)
  implementation(commonLibs.grpc.context)
  implementation(commonLibs.rxjava3)
  implementation(commonLibs.slf4j2.api)
  implementation(projects.hypertraceCoreGraphqlSpi)

  testImplementation(commonLibs.junit.jupiter)
  testImplementation(commonLibs.mockito.core)
  testImplementation(commonLibs.mockito.junit)

  testRuntimeOnly(commonLibs.grpc.netty)
}

tasks.test {
  useJUnitPlatform()
}
