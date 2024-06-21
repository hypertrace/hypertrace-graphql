plugins {
  `java-library`
  jacoco
  alias(commonLibs.plugins.hypertrace.jacoco)
}

dependencies {
  api(commonLibs.guice)
  api(commonLibs.graphql.java)
  api(projects.hypertraceCoreGraphqlAttributeStore)
  api(projects.hypertraceCoreGraphqlContext)
  api(commonLibs.rxjava3)
  api(localLibs.graphql.annotations)

  annotationProcessor(commonLibs.lombok)
  compileOnly(commonLibs.lombok)

  compileOnly(projects.hypertraceCoreGraphqlAttributeScopeConstants)

  implementation(projects.hypertraceCoreGraphqlDeserialization)
  implementation(projects.hypertraceCoreGraphqlSchemaUtils)

  testImplementation(commonLibs.junit.jupiter)
  testImplementation(commonLibs.mockito.core)
  testImplementation(commonLibs.mockito.junit)
}

tasks.test {
  useJUnitPlatform()
}
