plugins {
  `java-library`
  alias(libs.plugins.moduleplugin)
}

group = "org.braid.society.secret"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  mavenLocal()
}

dependencies {
  implementation(libs.logback)
  implementation(libs.jakarta.annotation)
  implementation(libs.jna)
  implementation(libs.guava)

  compileOnly(libs.lombok)
  testCompileOnly(libs.lombok)
//  implementation(files("../deps/lib-0.0.0.jar"))
//  implementation("jp.hiroshiba.voicevoxcore:voicevoxcore-android:0.16.0")

  testImplementation(platform("org.junit:junit-bom:5.10.0"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation(libs.bundles.tests.bundled)

  annotationProcessor(libs.lombok)
  annotationProcessor(libs.jakarta.annotation)
}

tasks.test {
  useJUnitPlatform()
}
