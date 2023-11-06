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

  annotationProcessor(commonLibs.lombok)
  compileOnly(commonLibs.lombok)

  implementation(localLibs.core.deserialization)
}

tasks.test {
  useJUnitPlatform()
}
