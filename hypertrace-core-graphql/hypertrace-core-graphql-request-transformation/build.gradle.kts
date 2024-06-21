plugins {
  `java-library`
  jacoco
  alias(commonLibs.plugins.hypertrace.jacoco)
}

dependencies {
  api(commonLibs.guice)
  api(projects.hypertraceCoreGraphqlCommonSchema)

  testAnnotationProcessor(commonLibs.lombok)
  testCompileOnly(commonLibs.lombok)
  testImplementation(commonLibs.junit.jupiter)
  testImplementation(commonLibs.mockito.core)
}

tasks.test {
  useJUnitPlatform()
}
