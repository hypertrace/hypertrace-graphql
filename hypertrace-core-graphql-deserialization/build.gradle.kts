plugins {
  `java-library`
  jacoco
  id("org.hypertrace.jacoco-report-plugin")
}

dependencies {
  api("com.google.inject:guice:4.2.3")
  api("com.fasterxml.jackson.core:jackson-databind:2.11.0")
  api("com.graphql-java:graphql-java:14.0")

  annotationProcessor("org.projectlombok:lombok:1.18.12")
  compileOnly("org.projectlombok:lombok:1.18.12")

  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11.0")
  implementation("org.slf4j:slf4j-api:1.7.3")

  testImplementation("org.junit.jupiter:junit-jupiter:5.5.1")
  testImplementation("org.mockito:mockito-core:3.2.4")
  testImplementation("org.mockito:mockito-junit-jupiter:3.2.4")
}

tasks.test {
  useJUnitPlatform()
}
