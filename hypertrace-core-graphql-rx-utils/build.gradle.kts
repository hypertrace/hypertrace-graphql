plugins {
  `java-library`
}

dependencies {
  api("io.reactivex.rxjava3:rxjava:3.0.2")
  api("com.google.inject:guice:4.2.3")
  implementation(project(":hypertrace-core-graphql-spi"))
  implementation("com.google.guava:guava:29.0-jre")
}
