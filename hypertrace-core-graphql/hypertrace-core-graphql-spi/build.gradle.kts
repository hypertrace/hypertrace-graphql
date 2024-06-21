plugins {
  `java-library`
}

dependencies {
  api(commonLibs.graphql.java)
  api(localLibs.graphql.annotations)
  api(commonLibs.jsr305)
}
