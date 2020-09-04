plugins {
  `java-library`
}

dependencies {
  api("com.google.inject:guice:4.2.3")
  api("io.reactivex.rxjava3:rxjava:3.0.2")
  api(project(":hypertrace-core-graphql-attribute-store"))
  api(project(":hypertrace-core-graphql-common-schema"))
}
