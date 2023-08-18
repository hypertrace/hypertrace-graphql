plugins {
  `java-platform`
}

javaPlatform {
  allowDependencies()
}

val attributeServiceVersion: String = "0.14.14"

dependencies {
  api(platform("io.grpc:grpc-bom:1.57.2"))
  api(platform("com.fasterxml.jackson:jackson-bom:2.15.2"))
  constraints {

    api("org.hypertrace.core.grpcutils:grpc-context-utils:0.12.2")
    api("org.hypertrace.core.grpcutils:grpc-client-utils:0.12.2")
    api("org.hypertrace.core.grpcutils:grpc-client-rx-utils:0.12.2")
    api("org.hypertrace.gateway.service:gateway-service-api:0.3.2")
    api("org.hypertrace.core.attribute.service:caching-attribute-service-client:${attributeServiceVersion}")
    api("org.hypertrace.core.attribute.service:attribute-service-api:${attributeServiceVersion}")

    api("com.google.inject:guice:5.1.0")
    api("com.graphql-java:graphql-java:19.6")
    api("io.github.graphql-java:graphql-java-annotations:9.1")
    api("org.slf4j:slf4j-api:1.7.36")
    api("io.reactivex.rxjava3:rxjava:3.1.6")
    api("com.google.protobuf:protobuf-java-util:3.21.7")

    api("org.projectlombok:lombok:1.18.24")
    api("com.google.code.findbugs:jsr305:3.0.2")
    api("com.typesafe:config:1.4.2")
    api("com.google.guava:guava:32.0.1-jre")
    api("com.graphql-java-kickstart:graphql-java-servlet:14.0.0")

    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
    api("org.apache.commons:commons-text:1.10.0")
    api("io.opentelemetry:opentelemetry-proto:1.1.0-alpha")

    runtime("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")
  }
}
