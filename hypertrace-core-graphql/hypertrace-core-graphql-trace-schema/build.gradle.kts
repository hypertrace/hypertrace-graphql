plugins {
  `java-library`
}

dependencies {
  api(commonLibs.guice)
  api(commonLibs.graphql.java)
  api(projects.hypertraceCoreGraphqlSpi)
  api(localLibs.graphql.annotations)

  annotationProcessor(commonLibs.lombok)
  compileOnly(commonLibs.lombok)

  compileOnly(projects.hypertraceCoreGraphqlAttributeScopeConstants)

  implementation(commonLibs.slf4j2.api)
  implementation(commonLibs.rxjava3)
  implementation(commonLibs.hypertrace.gatewayservice.api)
  implementation(commonLibs.protobuf.javautil)

  implementation(projects.hypertraceCoreGraphqlContext)
  implementation(projects.hypertraceCoreGraphqlGrpcUtils)
  implementation(projects.hypertraceCoreGraphqlCommonSchema)
  implementation(projects.hypertraceCoreGraphqlAttributeStore)
  implementation(projects.hypertraceCoreGraphqlDeserialization)
  implementation(projects.hypertraceCoreGraphqlRequestTransformation)
  implementation(projects.hypertraceCoreGraphqlSchemaUtils)
}
