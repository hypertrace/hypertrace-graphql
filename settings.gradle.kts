import org.hypertrace.gradle.dependency.DependencyPluginSettingExtension

rootProject.name = "hypertrace-core-graphql-root"

pluginManagement {
  repositories {
    mavenLocal()
    gradlePluginPortal()
    maven("https://hypertrace.jfrog.io/artifactory/maven")
  }
}

plugins {
  id("org.hypertrace.version-settings") version "0.2.1"
  id("org.hypertrace.dependency-settings") version "0.1.1"
}

configure<DependencyPluginSettingExtension> {
  catalogVersion.set("0.2.10")
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

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
include(":hypertrace-core-graphql-log-event-schema")
include(":hypertrace-core-graphql-request-transformation")
