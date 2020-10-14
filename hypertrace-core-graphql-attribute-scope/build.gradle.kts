plugins {
  `java-library`
}

dependencies {
  api("com.google.inject:guice")
  api("io.reactivex.rxjava3:rxjava")
  api(project(":hypertrace-core-graphql-attribute-store"))
  api(project(":hypertrace-core-graphql-common-schema"))
}
