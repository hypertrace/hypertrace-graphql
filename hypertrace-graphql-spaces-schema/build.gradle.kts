plugins {
  `java-library`
  jacoco
  alias(commonLibs.plugins.hypertrace.jacoco)
}

dependencies {
  api(commonLibs.guice)
  api(localLibs.core.context)
  api(commonLibs.rxjava3)
  api(localLibs.graphql.annotations)

  annotationProcessor(commonLibs.lombok)
  compileOnly(commonLibs.lombok)

  implementation(commonLibs.hypertrace.configservice.spaces.api)
  implementation(projects.hypertraceGraphqlServiceConfig)
  implementation(localLibs.core.schema.common)
  implementation(localLibs.core.grpc)
  implementation(localLibs.core.deserialization)
  implementation(projects.hypertraceGraphqlExplorerSchema)

  testImplementation(commonLibs.junit.jupiter)
  testImplementation(commonLibs.mockito.core)
  testImplementation(commonLibs.mockito.junit)
}

tasks.test {
  useJUnitPlatform()
}
