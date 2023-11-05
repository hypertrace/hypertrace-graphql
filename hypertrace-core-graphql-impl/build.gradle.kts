plugins {
  `java-library`
  jacoco
  alias(commonLibs.plugins.hypertrace.jacoco)
}

dependencies {
  api(projects.hypertraceCoreGraphqlSpi)
  api(localLibs.graphql.servlet)
  api(commonLibs.hypertrace.grpcutils.client)

  implementation(projects.hypertraceCoreGraphqlSchemaRegistry)
  implementation(projects.hypertraceCoreGraphqlContext)
  implementation(projects.hypertraceCoreGraphqlDeserialization)
  implementation(projects.hypertraceCoreGraphqlGrpcUtils)
  implementation(projects.hypertraceCoreGraphqlSchemaUtils)
  implementation(projects.hypertraceCoreGraphqlGatewayServiceUtils)
  implementation(projects.hypertraceCoreGraphqlAttributeStore)
  implementation(projects.hypertraceCoreGraphqlCommonSchema)
  implementation(projects.hypertraceCoreGraphqlMetadataSchema)
  implementation(projects.hypertraceCoreGraphqlSpanSchema)
  implementation(projects.hypertraceCoreGraphqlTraceSchema)
  implementation(projects.hypertraceCoreGraphqlAttributeScope)
  implementation(projects.hypertraceCoreGraphqlRxUtils)
  implementation(projects.hypertraceCoreGraphqlLogEventSchema)
  implementation(projects.hypertraceCoreGraphqlRequestTransformation)

  implementation(commonLibs.slf4j2.api)
  implementation(commonLibs.guice)

  testImplementation(commonLibs.junit.jupiter)
  testImplementation(commonLibs.mockito.core)
  testImplementation(commonLibs.mockito.junit)
}

tasks.test {
  useJUnitPlatform()
}
