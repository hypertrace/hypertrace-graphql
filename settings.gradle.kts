rootProject.name = "hypertrace-graphql-root"

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

includeBuild("./hypertrace-core-graphql")
include(":hypertrace-graphql-service")
include(":hypertrace-graphql-impl")
include(":hypertrace-graphql-gateway-service-metric-utils")
include(":hypertrace-graphql-metric-schema")
include(":hypertrace-graphql-entity-schema")
include(":hypertrace-graphql-explorer-schema")
include(":hypertrace-graphql-attribute-scope")
include(":hypertrace-graphql-explorer-context")
include(":hypertrace-graphql-entity-type")
include(":hypertrace-graphql-service-config")
include(":hypertrace-graphql-platform")
include(":hypertrace-graphql-spaces-schema")
include(":hypertrace-graphql-labels-schema-api")
include(":hypertrace-graphql-labels-schema-impl")
include(":hypertrace-graphql-label-application-rules-schema")