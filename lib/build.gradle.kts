plugins {
  `java-library`
  alias(libs.plugins.moduleplugin)
}

group = "org.braid.society.secret"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation(libs.bundles.bundled)

  testImplementation(platform("org.junit:junit-bom:5.10.0"))
  testImplementation("org.junit.jupiter:junit-jupiter")

  annotationProcessor(libs.bundles.annotations)
}

tasks.test {
  useJUnitPlatform()
}