plugins {
  java
  application
  id("org.hypertrace.docker-java-application-plugin")
  id("org.hypertrace.docker-publish-plugin")
}

dependencies {
  implementation("com.typesafe:config:1.4.0")
  implementation("org.hypertrace.core.serviceframework:platform-service-framework:0.1.15")
  implementation("org.slf4j:slf4j-api:1.7.30")

  implementation("org.eclipse.jetty:jetty-server:9.4.30.v20200611")
  implementation("org.eclipse.jetty:jetty-servlet:9.4.30.v20200611")
  implementation("org.eclipse.jetty:jetty-servlets:9.4.30.v20200611")

  implementation("com.graphql-java-kickstart:graphql-java-servlet:9.1.0")
  implementation(project(":hypertrace-core-graphql-impl"))
  implementation(project(":hypertrace-core-graphql-spi"))

  runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:2.13.3")
}

application {
  mainClassName = "org.hypertrace.core.serviceframework.PlatformServiceLauncher"
}
tasks.run<JavaExec> {
  jvmArgs = listOf("-Dservice.name=${project.name}")
}
