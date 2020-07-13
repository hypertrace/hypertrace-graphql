plugins {
  `java-library`
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
}

dependencies {
  api("com.google.inject:guice:4.2.3")
  api("com.graphql-java:graphql-java:14.0")
  api(project(":hypertrace-core-graphql-spi"))
  api("io.github.graphql-java:graphql-java-annotations:8.0")

  annotationProcessor("org.projectlombok:lombok:1.18.12")
  compileOnly("org.projectlombok:lombok:1.18.12")

  implementation("org.slf4j:slf4j-api:1.7.3")
  implementation("io.reactivex.rxjava3:rxjava:3.0.2")
  implementation("org.hypertrace.gateway.service:gateway-service-api:0.1.1")
  implementation("com.google.protobuf:protobuf-java-util:3.11.4")

  implementation(project(":hypertrace-core-graphql-context"))
  implementation(project(":hypertrace-core-graphql-grpc-utils"))
  implementation(project(":hypertrace-core-graphql-common-schema"))
  implementation(project(":hypertrace-core-graphql-attribute-store"))
  implementation(project(":hypertrace-core-graphql-deserialization"))

}
