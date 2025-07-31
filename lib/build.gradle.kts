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
  implementation(libs.logback)
  implementation(libs.jakarta.annotation)
  implementation(libs.jna)
  implementation(libs.guava)

  compileOnly(libs.lombok)
  testCompileOnly(libs.lombok)

  testImplementation(platform("org.junit:junit-bom:5.10.0"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  testImplementation(libs.bundles.tests.bundled)

  annotationProcessor(libs.lombok)
  annotationProcessor(libs.jakarta.annotation)
}

tasks.test {
  useJUnitPlatform()
}
