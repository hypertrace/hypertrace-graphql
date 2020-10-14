plugins {
  `java-library`
  jacoco
  id("org.hypertrace.jacoco-report-plugin")
}

dependencies {
  api(project(":hypertrace-core-graphql-spi"))
  api("com.graphql-java-kickstart:graphql-java-servlet")

  implementation(project(":hypertrace-core-graphql-schema-registry"))
  implementation(project(":hypertrace-core-graphql-context"))
  implementation(project(":hypertrace-core-graphql-deserialization"))
  implementation(project(":hypertrace-core-graphql-grpc-utils"))
  implementation(project(":hypertrace-core-graphql-schema-utils"))
  implementation(project(":hypertrace-core-graphql-gateway-service-utils"))
  implementation(project(":hypertrace-core-graphql-attribute-store"))
  implementation(project(":hypertrace-core-graphql-common-schema"))
  implementation(project(":hypertrace-core-graphql-metadata-schema"))
  implementation(project(":hypertrace-core-graphql-span-schema"))
  implementation(project(":hypertrace-core-graphql-trace-schema"))
  implementation(project(":hypertrace-core-graphql-attribute-scope"))
  implementation(project(":hypertrace-core-graphql-rx-utils"))

  implementation("org.slf4j:slf4j-api")
  implementation("com.google.inject:guice")

  runtimeOnly("io.grpc:grpc-netty")

  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation("org.mockito:mockito-core")
  testImplementation("org.mockito:mockito-junit-jupiter")
}

tasks.test {
  useJUnitPlatform()
}
