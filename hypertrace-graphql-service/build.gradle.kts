plugins {
  java
  application
  id("org.hypertrace.docker-java-application-plugin")
  id("org.hypertrace.docker-publish-plugin")
}

dependencies {
  implementation("com.typesafe:config")
  implementation("org.hypertrace.core.serviceframework:platform-service-framework:0.1.20")
  implementation("org.slf4j:slf4j-api")

  implementation("org.eclipse.jetty:jetty-server:9.4.36.v20210114")
  implementation("org.eclipse.jetty:jetty-servlet:9.4.36.v20210114")
  implementation("org.eclipse.jetty:jetty-servlets:9.4.36.v20210114")

  implementation("com.graphql-java-kickstart:graphql-java-servlet")
  implementation(project(":hypertrace-graphql-impl"))
  implementation(project(":hypertrace-graphql-service-config"))

  annotationProcessor("org.projectlombok:lombok")
  compileOnly("org.projectlombok:lombok")

  runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl")

  annotationProcessor(platform(project(":hypertrace-graphql-platform")))
  compileOnly(platform(project(":hypertrace-graphql-platform")))
}

application {
  mainClass.set("org.hypertrace.core.serviceframework.PlatformServiceLauncher")
}
tasks.run<JavaExec> {
  jvmArgs = listOf("-Dservice.name=${project.name}")
}

hypertraceDocker {
  defaultImage {
    javaApplication {
      port.set(23431)
    }
  }
}
