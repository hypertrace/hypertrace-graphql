plugins {
  `java-library`
}

dependencies {
  api(commonLibs.guice)
  api(commonLibs.rxjava3)
  implementation(localLibs.core.attribute.store)
  implementation(localLibs.core.schema.common)
  implementation(projects.hypertraceGraphqlExplorerSchema)

  compileOnly(projects.hypertraceGraphqlAttributeScope)
}
