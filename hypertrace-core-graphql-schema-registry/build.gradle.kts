plugins {
  `java-library`
  jacoco
  id("org.hypertrace.jacoco-report-plugin")
}

dependencies {
  api("com.graphql-java:graphql-java:14.0")

  implementation("org.slf4j:slf4j-api:1.7.3")
  implementation("com.google.inject:guice:4.2.3")

  implementation(project(":hypertrace-core-graphql-spi"))
  implementation("io.github.graphql-java:graphql-java-annotations:8.0")

  testImplementation("org.junit.jupiter:junit-jupiter:5.5.1")
  testImplementation("org.mockito:mockito-core:3.2.4")
  testImplementation("org.mockito:mockito-junit-jupiter:3.2.4")
}

tasks.test {
  useJUnitPlatform()
}
