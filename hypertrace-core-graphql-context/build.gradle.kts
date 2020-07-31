plugins {
  `java-library`
  jacoco
  id("org.hypertrace.jacoco-report-plugin")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
}

dependencies {
  api("com.google.inject:guice:4.2.3")
  api("com.graphql-java:graphql-java:14.0")
  api("com.graphql-java-kickstart:graphql-java-servlet:9.1.0")

  implementation(project(":hypertrace-core-graphql-spi"))

  testImplementation("org.junit.jupiter:junit-jupiter:5.5.1")
  testImplementation("org.mockito:mockito-core:3.2.4")
  testImplementation("org.mockito:mockito-junit-jupiter:3.2.4")
}

tasks.test {
  useJUnitPlatform()
}
