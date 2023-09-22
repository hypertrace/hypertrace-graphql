plugins {
  id("org.hypertrace.repository-plugin") version "0.4.2"
  id("org.hypertrace.ci-utils-plugin") version "0.3.2"
  id("org.hypertrace.jacoco-report-plugin") version "0.2.1" apply false
  id("org.hypertrace.docker-java-application-plugin") version "0.9.9" apply false
  id("org.hypertrace.docker-publish-plugin") version "0.9.9" apply false
  id("org.hypertrace.code-style-plugin") version "1.2.0" apply false
  id("org.owasp.dependencycheck") version "8.2.1"
}

subprojects {
  group = "org.hypertrace.core.graphql"
  pluginManager.withPlugin("java") {
    apply(plugin = "org.hypertrace.code-style-plugin")
    configure<JavaPluginExtension> {
      sourceCompatibility = JavaVersion.VERSION_11
      targetCompatibility = JavaVersion.VERSION_11
    }
  }

  pluginManager.withPlugin("java-library") {
    dependencies {
      "api"(platform(project(":hypertrace-core-graphql-platform")))
      "annotationProcessor"(platform(project(":hypertrace-core-graphql-platform")))
      "testAnnotationProcessor"(platform(project(":hypertrace-core-graphql-platform")))
      "testImplementation"(platform(project(":hypertrace-core-graphql-test-platform")))
      "compileOnly"(platform(project(":hypertrace-core-graphql-platform")))
    }
  }
}

dependencyCheck {
  format = org.owasp.dependencycheck.reporting.ReportGenerator.Format.ALL.toString()
  suppressionFile = "owasp-suppressions.xml"
  scanConfigurations.add("runtimeClasspath")
  failBuildOnCVSS = 7.0F
}
