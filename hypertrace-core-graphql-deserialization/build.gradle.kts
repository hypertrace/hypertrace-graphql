plugins {
  `java-library`
  jacoco
  alias(commonLibs.plugins.hypertrace.jacoco)
}

dependencies {
  api(commonLibs.guice)
  api(commonLibs.jackson.databind)
  api(commonLibs.graphql.java)

  annotationProcessor(commonLibs.lombok)
  compileOnly(commonLibs.lombok)

  implementation(commonLibs.jackson.datatype.jsr310)
  implementation(commonLibs.jackson.datatype.jdk8)
  implementation(commonLibs.slf4j2.api)

  testImplementation(commonLibs.junit.jupiter)
  testImplementation(commonLibs.mockito.core)
  testImplementation(commonLibs.mockito.junit)
}

tasks.test {
  useJUnitPlatform()
}
