plugins {
  java
  application
  alias(commonLibs.plugins.hypertrace.docker.application)
  alias(commonLibs.plugins.hypertrace.docker.publish)
}

dependencies {
  implementation(commonLibs.typesafe.config)
  implementation(commonLibs.hypertrace.framework.http)
  implementation(commonLibs.slf4j2.api)
  implementation(localLibs.graphql.servlet)
  implementation(projects.hypertraceGraphqlImpl)
  implementation(projects.hypertraceGraphqlServiceConfig)

  annotationProcessor(commonLibs.lombok)
  compileOnly(commonLibs.lombok)

  runtimeOnly(commonLibs.grpc.netty)
  runtimeOnly(commonLibs.log4j.slf4j2.impl)
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
