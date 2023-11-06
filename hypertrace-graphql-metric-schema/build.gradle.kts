plugins {
  `java-library`
}

dependencies {
  api(commonLibs.guice)
  api(commonLibs.graphql.java)
  api(localLibs.core.attribute.store)
  api(localLibs.core.context)
  api(commonLibs.rxjava3)
  api(localLibs.graphql.annotations)

  annotationProcessor(commonLibs.lombok)
  compileOnly(commonLibs.lombok)

  implementation(localLibs.core.deserialization)
  implementation(localLibs.core.schema.utils)
  implementation(localLibs.core.schema.common)
}
