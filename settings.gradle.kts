rootProject.name = "hypertrace-core-graphql"

pluginManagement {
  repositories {
    mavenLocal()
    gradlePluginPortal()
    maven("https://hypertrace.jfrog.io/artifactory/maven")
  }
}

plugins {
  id("org.hypertrace.version-settings") version "0.2.0"
}

include(":hypertrace-core-graphql-service")
include(":hypertrace-core-graphql-impl")
include(":hypertrace-core-graphql-spi")
include(":hypertrace-core-graphql-context")
include(":hypertrace-core-graphql-schema-registry")
include(":hypertrace-core-graphql-attribute-store")
include(":hypertrace-core-graphql-deserialization")
include(":hypertrace-core-graphql-grpc-utils")
include(":hypertrace-core-graphql-schema-utils")
include(":hypertrace-core-graphql-gateway-service-utils")
include(":hypertrace-core-graphql-common-schema")
include(":hypertrace-core-graphql-metadata-schema")
include(":hypertrace-core-graphql-span-schema")
include(":hypertrace-core-graphql-trace-schema")
include(":hypertrace-core-graphql-attribute-scope")
include(":hypertrace-core-graphql-attribute-scope-constants")
include(":hypertrace-core-graphql-rx-utils")
include(":hypertrace-core-graphql-platform")
include(":hypertrace-core-graphql-test-platform")
