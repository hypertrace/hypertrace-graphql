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
  api(localLibs.core.schema.common)

  annotationProcessor(commonLibs.lombok)
  compileOnly(commonLibs.lombok)
  compileOnly(projects.hypertraceGraphqlAttributeScope)

  implementation(commonLibs.slf4j2.api)
  implementation(commonLibs.rxjava3)
  implementation(commonLibs.hypertrace.gatewayservice.api)
  implementation(commonLibs.protobuf.javautil)
  implementation(commonLibs.guava)
  implementation(commonLibs.hypertrace.grpcutils.client)
  implementation(projects.hypertraceGraphqlLabelsSchemaApi)

  implementation(localLibs.core.context)
  implementation(localLibs.core.grpc)
  implementation(localLibs.core.schema.utils)
  implementation(localLibs.core.attribute.store)
  implementation(localLibs.core.deserialization)
  implementation(localLibs.core.rxutils)

  testImplementation(commonLibs.junit.jupiter)
  testImplementation(commonLibs.mockito.core)
  testImplementation(commonLibs.mockito.junit)
  testAnnotationProcessor(commonLibs.lombok)
  testCompileOnly(commonLibs.lombok)
}

tasks.test {
  useJUnitPlatform()
}
