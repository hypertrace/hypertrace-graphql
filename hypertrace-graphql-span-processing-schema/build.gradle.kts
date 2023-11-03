plugins {
  `java-library`
  jacoco
  alias(commonLibs.plugins.hypertrace.jacoco)
}

dependencies {
  api(commonLibs.guice)
  api(commonLibs.graphql.java)
  api(localLibs.core.schema.common)
  api(localLibs.core.spi)
  api(localLibs.graphql.annotations)

  annotationProcessor(commonLibs.lombok)
  compileOnly(commonLibs.lombok)

  implementation(projects.hypertraceGraphqlServiceConfig)
  implementation(commonLibs.hypertrace.configservice.spanprocessing.api)
  implementation(localLibs.core.context)
  implementation(localLibs.core.grpc)
  implementation(localLibs.core.deserialization)
  implementation(commonLibs.slf4j2.api)
  implementation(commonLibs.rxjava3)
}
