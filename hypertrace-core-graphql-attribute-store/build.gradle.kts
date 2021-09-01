plugins {
  `java-library`
  jacoco
  id("org.hypertrace.jacoco-report-plugin")
}

dependencies {
  api("com.google.inject:guice")
  api(project(":hypertrace-core-graphql-spi"))
  api(project(":hypertrace-core-graphql-context"))

  implementation("org.slf4j:slf4j-api")
  implementation("io.reactivex.rxjava3:rxjava")
  implementation("com.google.guava:guava")

  implementation("org.hypertrace.core.attribute.service:caching-attribute-service-client")
  implementation("org.hypertrace.core.grpcutils:grpc-client-rx-utils")
  implementation(project(":hypertrace-core-graphql-grpc-utils"))
  implementation(project(":hypertrace-core-graphql-rx-utils"))

  annotationProcessor("org.projectlombok:lombok")
  compileOnly("org.projectlombok:lombok")

  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation("org.mockito:mockito-core")
  testImplementation("org.mockito:mockito-junit-jupiter")
}

tasks.test {
  useJUnitPlatform()
}
