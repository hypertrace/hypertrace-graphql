plugins {
  `java-library`
  jacoco
  alias(commonLibs.plugins.hypertrace.jacoco)
}

dependencies {
  api(commonLibs.guice)
  api(commonLibs.graphql.java)
  api(localLibs.core.spi)
  api(localLibs.graphql.annotations)
  api(projects.hypertraceGraphqlMetricSchema)

  annotationProcessor(commonLibs.lombok)
  compileOnly(commonLibs.lombok)

  implementation(commonLibs.slf4j2.api)
  implementation(commonLibs.rxjava3)
  implementation(commonLibs.hypertrace.gatewayservice.api)
  implementation(commonLibs.protobuf.javautil)

  implementation(localLibs.core.context)
  implementation(localLibs.core.grpc)
  implementation(localLibs.core.schema.common)
  implementation(localLibs.core.attribute.store)
  implementation(localLibs.core.deserialization)
  implementation(localLibs.core.schema.utils)
  implementation(localLibs.core.rxutils)
  implementation(localLibs.core.request.transformation)

  testImplementation(commonLibs.junit.jupiter)
  testImplementation(commonLibs.mockito.core)
  testImplementation(commonLibs.mockito.junit)
}

tasks.test {
  useJUnitPlatform()
}
