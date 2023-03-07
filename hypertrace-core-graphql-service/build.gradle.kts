plugins {
  java
  application
  id("org.hypertrace.docker-java-application-plugin")
  id("org.hypertrace.docker-publish-plugin")
}

dependencies {
  implementation(platform(project(":hypertrace-core-graphql-platform")))

  implementation("org.hypertrace.core.serviceframework:platform-http-service-framework:0.1.50")
  implementation("org.slf4j:slf4j-api")

  implementation("com.graphql-java-kickstart:graphql-java-servlet")
  implementation(project(":hypertrace-core-graphql-impl"))
  implementation(project(":hypertrace-core-graphql-spi"))

  runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl")
  runtimeOnly("io.grpc:grpc-netty")
}

application {
  mainClass.set("org.hypertrace.core.serviceframework.PlatformServiceLauncher")
}
