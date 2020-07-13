plugins {
  `java-library`
  jacoco
  id("org.hypertrace.jacoco-report-plugin")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
}

dependencies {
  api("com.google.inject:guice:4.2.3")
  api("org.hypertrace.gateway.service:gateway-service-api:0.1.1")
  api(project(":hypertrace-core-graphql-attribute-store"))
  api("io.reactivex.rxjava3:rxjava:3.0.2")
  api(project(":hypertrace-core-graphql-common-schema"))

  testImplementation("org.junit.jupiter:junit-jupiter:5.5.1")
  testImplementation("org.mockito:mockito-core:3.2.4")
  testImplementation("org.mockito:mockito-junit-jupiter:3.2.4")
}

tasks.test {
  useJUnitPlatform()
}