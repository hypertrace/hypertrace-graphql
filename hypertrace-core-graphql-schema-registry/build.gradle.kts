plugins {
  `java-library`
  jacoco
  alias(commonLibs.plugins.hypertrace.jacoco)
}

dependencies {
  api(commonLibs.graphql.java)

  implementation(commonLibs.slf4j2.api)
  implementation(commonLibs.guice)

  implementation(projects.hypertraceCoreGraphqlSpi)
  implementation(localLibs.graphql.annotations)

  testImplementation(commonLibs.junit.jupiter)
  testImplementation(commonLibs.mockito.core)
  testImplementation(commonLibs.mockito.junit)
}

tasks.test {
  useJUnitPlatform()
}
