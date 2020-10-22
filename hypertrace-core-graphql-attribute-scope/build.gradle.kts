plugins {
  `java-library`
}

dependencies {
  api("com.google.inject:guice")
  api("io.reactivex.rxjava3:rxjava")
  api(project(":hypertrace-core-graphql-attribute-store"))
  api(project(":hypertrace-core-graphql-common-schema"))
  // These are kept in a separate project so they can be referenced by other projects without circular dependencies
  compileOnly(project(":hypertrace-core-graphql-attribute-scope-constants"))
}
