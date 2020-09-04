plugins {
  `java-library`
  jacoco
  id("org.hypertrace.jacoco-report-plugin")
}

dependencies {
  api("com.google.inject:guice:4.2.3")
  api("com.graphql-java:graphql-java:14.0")
  api(project(":hypertrace-core-graphql-attribute-store"))
  api(project(":hypertrace-core-graphql-context"))
  api("io.reactivex.rxjava3:rxjava:3.0.2")
  api("io.github.graphql-java:graphql-java-annotations:8.0")

  annotationProcessor("org.projectlombok:lombok:1.18.12")
  compileOnly("org.projectlombok:lombok:1.18.12")

  implementation(project(":hypertrace-core-graphql-deserialization"))
  implementation(project(":hypertrace-core-graphql-schema-utils"))
  testImplementation("org.junit.jupiter:junit-jupiter:5.5.1")
  testImplementation("org.mockito:mockito-core:3.2.4")
  testImplementation("org.mockito:mockito-junit-jupiter:3.2.4")
}

tasks.test {
  useJUnitPlatform()
}
