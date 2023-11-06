plugins {
  `java-library`
  jacoco
  alias(commonLibs.plugins.hypertrace.jacoco)
}

dependencies {
  api(localLibs.core.spi)
  api(localLibs.graphql.servlet)
  api(projects.hypertraceGraphqlServiceConfig)
  api(commonLibs.hypertrace.grpcutils.client)

  implementation(localLibs.core.schema.registry)
  implementation(localLibs.core.context)
  implementation(localLibs.core.deserialization)
  implementation(localLibs.core.grpc)
  implementation(localLibs.core.schema.utils)
  implementation(localLibs.core.gateway.utils)
  implementation(localLibs.core.rxutils)
  implementation(projects.hypertraceGraphqlAttributeScope)
  implementation(projects.hypertraceGraphqlGatewayServiceMetricUtils)
  implementation(localLibs.core.attribute.store)
  implementation(localLibs.core.schema.common)
  implementation(projects.hypertraceGraphqlMetricSchema)
  implementation(localLibs.core.schema.metadata)
  implementation(localLibs.core.schema.spans)
  implementation(localLibs.core.schema.logevents)
  implementation(localLibs.core.schema.traces)
  implementation(localLibs.core.request.transformation)
  implementation(projects.hypertraceGraphqlEntitySchema)
  implementation(projects.hypertraceGraphqlExplorerSchema)
  implementation(projects.hypertraceGraphqlExplorerContext)
  implementation(projects.hypertraceGraphqlEntityType)
  implementation(projects.hypertraceGraphqlSpacesSchema)
  implementation(projects.hypertraceGraphqlLabelsSchemaImpl)
  implementation(projects.hypertraceGraphqlSpanProcessingSchema)

  implementation(commonLibs.slf4j2.api)
  implementation(commonLibs.guice)

  testImplementation(commonLibs.junit.jupiter)
  testImplementation(commonLibs.mockito.core)
  testImplementation(commonLibs.mockito.junit)
}

tasks.test {
  useJUnitPlatform()
}
