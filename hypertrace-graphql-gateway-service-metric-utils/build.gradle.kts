plugins {
  `java-library`
}

dependencies {
  api(commonLibs.guice)
  api(commonLibs.hypertrace.gatewayservice.api)
  api(localLibs.core.attribute.store)
  api(commonLibs.rxjava3)
  api(localLibs.core.schema.common)
  api(projects.hypertraceGraphqlMetricSchema)
  api(localLibs.core.gateway.utils)

  annotationProcessor(commonLibs.lombok)
  compileOnly(commonLibs.lombok)
}
