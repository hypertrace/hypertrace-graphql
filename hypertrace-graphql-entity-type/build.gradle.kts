plugins {
  `java-library`
}

dependencies {
  api(commonLibs.guice)
  api(commonLibs.rxjava3)
  implementation(localLibs.core.attribute.store)
  implementation(localLibs.core.schema.common)
  implementation(commonLibs.hypertrace.entityservice.types.rxclient)
  implementation(localLibs.core.grpc)
  implementation(projects.hypertraceGraphqlEntitySchema)
  implementation(projects.hypertraceGraphqlServiceConfig)

  compileOnly(projects.hypertraceGraphqlAttributeScope)
}
