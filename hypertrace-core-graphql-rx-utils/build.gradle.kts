plugins {
  `java-library`
}

dependencies {
  api(commonLibs.rxjava3)
  api(commonLibs.guice)
  implementation(projects.hypertraceCoreGraphqlSpi)
  implementation(commonLibs.guava)
}
