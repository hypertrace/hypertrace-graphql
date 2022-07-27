plugins {
  java
  application
  id("org.hypertrace.docker-java-application-plugin")
  id("org.hypertrace.docker-publish-plugin")
}

dependencies {
  implementation("com.typesafe:config")
  implementation("org.hypertrace.core.serviceframework:platform-service-framework:0.1.21")
  implementation("org.slf4j:slf4j-api")

  implementation("org.hypertrace.core.serviceframework:platform-http-service-framework:0.2.0-SNAPSHOT")

  implementation("com.graphql-java-kickstart:graphql-java-servlet")
  implementation(project(":hypertrace-graphql-impl"))
  implementation(project(":hypertrace-graphql-service-config"))

  annotationProcessor("org.projectlombok:lombok")
  compileOnly("org.projectlombok:lombok")

  runtimeOnly("io.grpc:grpc-netty")
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
