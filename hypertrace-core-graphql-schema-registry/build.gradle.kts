plugins {
  `java-library`
  jacoco
  id("org.hypertrace.jacoco-report-plugin")
}

dependencies {
  api("com.graphql-java:graphql-java")

  implementation("org.slf4j:slf4j-api")
  implementation("com.google.inject:guice")

  implementation(project(":hypertrace-core-graphql-spi"))
  implementation("io.github.graphql-java:graphql-java-annotations")

  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation("org.mockito:mockito-core")
  testImplementation("org.mockito:mockito-junit-jupiter")
}

tasks.test {
  useJUnitPlatform()
}
