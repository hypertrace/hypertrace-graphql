plugins {
  java
  application
  id("org.hypertrace.docker-java-application-plugin")
  id("org.hypertrace.docker-publish-plugin")
}

dependencies {
  implementation("com.typesafe:config")
  implementation("org.hypertrace.core.serviceframework:platform-service-framework:0.1.16")
  implementation("org.slf4j:slf4j-api")

  implementation("org.eclipse.jetty:jetty-server:9.4.30.v20200611")
  implementation("org.eclipse.jetty:jetty-servlet:9.4.30.v20200611")
  implementation("org.eclipse.jetty:jetty-servlets:9.4.30.v20200611")

  implementation("com.graphql-java-kickstart:graphql-java-servlet")
  implementation(project(":hypertrace-graphql-impl"))
  implementation(project(":hypertrace-graphql-service-config"))

  runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl")
}

application {
  mainClass.set("org.hypertrace.core.serviceframework.PlatformServiceLauncher")
}
tasks.run<JavaExec> {
  jvmArgs = listOf("-Dservice.name=${project.name}")
}

