plugins {
    `java-library`
}

dependencies {
    api("com.google.inject:guice")
    api("com.graphql-java:graphql-java")
    api(project(":hypertrace-core-graphql-spi"))
    api("io.github.graphql-java:graphql-java-annotations")

    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")

    compileOnly(project(":hypertrace-core-graphql-attribute-scope-constants"))

    implementation("org.slf4j:slf4j-api")
    implementation("io.reactivex.rxjava3:rxjava")
    implementation("org.hypertrace.gateway.service:gateway-service-api")
    implementation("com.google.protobuf:protobuf-java-util")

    implementation(project(":hypertrace-core-graphql-context"))
    implementation(project(":hypertrace-core-graphql-grpc-utils"))
    implementation(project(":hypertrace-core-graphql-common-schema"))
    implementation(project(":hypertrace-core-graphql-attribute-store"))
    implementation(project(":hypertrace-core-graphql-deserialization"))
    implementation(project(":hypertrace-core-graphql-schema-utils"))

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.fasterxml.jackson.core:jackson-databind")
    testImplementation(project(":hypertrace-core-graphql-gateway-service-utils"))
    testAnnotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
}

tasks.test {
    useJUnitPlatform()
}
