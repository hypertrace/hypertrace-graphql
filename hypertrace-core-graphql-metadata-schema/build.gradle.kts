plugins {
  `java-library`
  jacoco
  alias(commonLibs.plugins.hypertrace.jacoco)
}

dependencies {
  api(commonLibs.guice)
  api(commonLibs.graphql.java)
  api(projects.hypertraceCoreGraphqlSpi)
  api(localLibs.graphql.annotations)

  annotationProcessor(commonLibs.lombok)
  compileOnly(commonLibs.lombok)

  implementation(commonLibs.slf4j2.api)
  implementation(commonLibs.rxjava3)
  implementation(projects.hypertraceCoreGraphqlContext)
  implementation(projects.hypertraceCoreGraphqlCommonSchema)
  implementation(projects.hypertraceCoreGraphqlAttributeStore)

  testImplementation(commonLibs.junit.jupiter)
  testImplementation(commonLibs.mockito.core)
  testImplementation(commonLibs.mockito.junit)
}

tasks.test {
  useJUnitPlatform()
}
