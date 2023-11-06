plugins {
  `java-library`
}

dependencies {
  api(commonLibs.guice)
  api(commonLibs.rxjava3)
  api(localLibs.core.attribute.constants)
  api(localLibs.core.schema.common)
  implementation(localLibs.core.attribute.store)
}
