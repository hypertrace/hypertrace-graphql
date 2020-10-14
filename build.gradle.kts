plugins {
  id("org.hypertrace.repository-plugin") version "0.2.0"
  id("org.hypertrace.ci-utils-plugin") version "0.1.1"
  id("org.hypertrace.jacoco-report-plugin") version "0.1.0" apply false
  id("org.hypertrace.docker-java-application-plugin") version "0.4.0" apply false
  id("org.hypertrace.docker-publish-plugin") version "0.4.0" apply false
}

subprojects {
  group = "org.hypertrace.core.graphql"

  pluginManager.withPlugin("java") {
    configure<JavaPluginExtension> {
      sourceCompatibility = JavaVersion.VERSION_11
      targetCompatibility = JavaVersion.VERSION_11
    }
  }

  pluginManager.withPlugin("java-library") {

    dependencies {
      "api"(platform(project(":hypertrace-core-graphql-platform")))
      "annotationProcessor"(platform(project(":hypertrace-core-graphql-platform")))
      "testImplementation"(platform(project(":hypertrace-core-graphql-test-platform")))
      "compileOnly"(platform(project(":hypertrace-core-graphql-platform")))
    }
  }
}
