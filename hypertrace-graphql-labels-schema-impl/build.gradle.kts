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
  api(localLibs.core.schema.common)
  api(projects.hypertraceGraphqlLabelsSchemaApi)

  annotationProcessor(commonLibs.lombok)
  compileOnly(commonLibs.lombok)

  implementation(commonLibs.slf4j2.api)
  implementation(commonLibs.rxjava3)
  implementation(commonLibs.hypertrace.configservice.labels.api)
  implementation(commonLibs.hypertrace.configservice.labelapplication.api)
  implementation(commonLibs.protobuf.javautil)
  implementation(commonLibs.guava)

  implementation(localLibs.core.context)
  implementation(localLibs.core.grpc)
  implementation(localLibs.core.schema.utils)
  implementation(localLibs.core.rxutils)
  implementation(localLibs.core.deserialization)

  implementation(projects.hypertraceGraphqlEntitySchema)
  implementation(projects.hypertraceGraphqlServiceConfig)
}

tasks.test {
  useJUnitPlatform()
}
