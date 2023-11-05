plugins {
  java
  application
  alias(commonLibs.plugins.hypertrace.docker.application)
  alias(commonLibs.plugins.hypertrace.docker.publish)
}

dependencies {
  implementation(commonLibs.hypertrace.framework.http)
  implementation(commonLibs.slf4j2.api)

  implementation(localLibs.graphql.servlet)
  implementation(projects.hypertraceCoreGraphqlImpl)
  implementation(projects.hypertraceCoreGraphqlSpi)

  runtimeOnly(commonLibs.log4j.slf4j2.impl)
  runtimeOnly(commonLibs.grpc.netty)
}

application {
  mainClass.set("org.hypertrace.core.serviceframework.PlatformServiceLauncher")
}
