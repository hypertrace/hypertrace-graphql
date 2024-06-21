plugins {
  `java-library`
}

dependencies {
  api(commonLibs.guice)
  api(commonLibs.rxjava3)
  api(projects.hypertraceCoreGraphqlAttributeStore)
  api(projects.hypertraceCoreGraphqlCommonSchema)
  // These are kept in a separate project so they can be referenced by other projects without circular dependencies
  compileOnly(projects.hypertraceCoreGraphqlAttributeScopeConstants)
}
