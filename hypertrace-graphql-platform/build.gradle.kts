plugins {
  `java-platform`
}

javaPlatform {
  allowDependencies()
}

dependencies {
  api(platform("org.hypertrace.core.graphql:hypertrace-core-graphql-platform"))
  constraints {
    api("org.hypertrace.entity.service:entity-type-service-rx-client:0.5.6")
    api("org.hypertrace.config.service:spaces-config-service-api:0.1.1")
    //api("org.hypertrace.config.service:labels-config-service-api:0.1.8")
    api("org.hypertrace.config.service:labels-config-service-api:0.2.0-SNAPSHOT")
    api("org.hypertrace.config.service:label-application-rule-config-service-api:0.1.14")
  }
}
