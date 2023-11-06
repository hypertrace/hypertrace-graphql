plugins {
  alias(commonLibs.plugins.hypertrace.ciutils)
  alias(commonLibs.plugins.hypertrace.codestyle) apply false
  alias(commonLibs.plugins.owasp.dependencycheck)
}

subprojects {
  group = "org.hypertrace.graphql"

  pluginManager.withPlugin("java") {
    apply(plugin = commonLibs.plugins.hypertrace.codestyle.get().pluginId)
    configure<JavaPluginExtension> {
      sourceCompatibility = JavaVersion.VERSION_11
      targetCompatibility = JavaVersion.VERSION_11
    }
  }
}

dependencyCheck {
  format = org.owasp.dependencycheck.reporting.ReportGenerator.Format.ALL.toString()
  suppressionFile = "owasp-suppressions.xml"
  scanConfigurations.add("runtimeClasspath")
  failBuildOnCVSS = 7.0F
}
