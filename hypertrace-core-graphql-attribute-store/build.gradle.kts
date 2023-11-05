plugins {
  `java-library`
  jacoco
  alias(commonLibs.plugins.hypertrace.jacoco)
}

dependencies {
  api(commonLibs.guice)
  api(projects.hypertraceCoreGraphqlSpi)
  api(projects.hypertraceCoreGraphqlContext)

  implementation(commonLibs.slf4j2.api)
  implementation(commonLibs.rxjava3)
  implementation(commonLibs.guava)

  implementation(commonLibs.hypertrace.attributeservice.cachingclient)
  implementation(commonLibs.hypertrace.attributeservice.api)
  implementation(commonLibs.hypertrace.grpcutils.rx.client)
  implementation(projects.hypertraceCoreGraphqlGrpcUtils)
  implementation(projects.hypertraceCoreGraphqlRxUtils)

  annotationProcessor(commonLibs.lombok)
  compileOnly(commonLibs.lombok)

  testImplementation(commonLibs.junit.jupiter)
  testImplementation(commonLibs.mockito.core)
  testImplementation(commonLibs.mockito.junit)
}

tasks.test {
  useJUnitPlatform()
}
